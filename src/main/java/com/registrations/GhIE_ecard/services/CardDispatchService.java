package com.registrations.GhIE_ecard.services;

import com.registrations.GhIE_ecard.models.CardProcessingResult;
import com.registrations.GhIE_ecard.models.StudentMember;
import com.registrations.GhIE_ecard.repositories.AdminRepository;
import com.registrations.GhIE_ecard.repositories.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;



@Service
public class CardDispatchService {
    private static final Logger log = LoggerFactory.getLogger(CardDispatchService.class);
    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    @Autowired
    private final FastApiClientService clientService;

    public CardDispatchService(RestClient restClient, AdminRepository adminRepository,
                               FastApiClientService clientService, MemberRepository memberRepository) {
        this.adminRepository = adminRepository;
        this.clientService = clientService;
        this.memberRepository = memberRepository;
    }

    // Method handles sending cards for single members
    public String processSingleCard(String memberId){
       StudentMember studentMember = memberRepository.findByMemberId(memberId).orElseThrow(()
               -> new RuntimeException("Member not found with ID: " + memberId));
       if(studentMember.getEmailSent() == true){
           log.info("Card already sent for member: {}", memberId);
           return "ALREADY SENT";

       }
       try {
           // call fastapi on this single member and add to the thread
          Boolean success = clientService.callFastApi(studentMember).join();
          if (success) {
              studentMember.setEmailSent(true);
              studentMember.setEmailSentAt(LocalDateTime.now());
              memberRepository.save(studentMember);
              return "SUCCESS";
          }

       } catch (Exception e) {
           log.error("Failed to process single card for {}: {}", memberId, e.getMessage());
       }
       return "FAILED";

    }

 // Method to handle sending cards for bulk members
    public CardProcessingResult processAllPendingCards() {
        // get list of pending members from database
        List<StudentMember> pendingStudentMembers = memberRepository.findByEmailSentFalse();

        /* Implementing Multithreading to speed up card generation and automation Process */

        // Start a stream
        // call fastApi on each member to run in async to speed up
        List<CompletableFuture<Boolean>> futures =
                (List<CompletableFuture<Boolean>>) pendingStudentMembers.stream().map(
                member -> clientService.callFastApi(member).thenApply(
                        success -> {
                            if (success){
                                member.setEmailSent(true);
                                member.setEmailSentAt(LocalDateTime.now());
                                memberRepository.save(member);
                                return true;
                            }
                            return false;
                        })).toList();
        // Big Wait, this line below ensures all futures or all workers are finished
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // counts how many true we have in our opened receipts or finished tasks
        int successCounts = (int) futures.stream().map
                        (CompletableFuture::join) // open receipts and gets the results (true/ false)
                .filter(result -> result == true) // only keep the receipts that returned true
                .count();

        int failureCounts = pendingStudentMembers.size() - successCounts;

        return new CardProcessingResult(successCounts, failureCounts);
    }
}
