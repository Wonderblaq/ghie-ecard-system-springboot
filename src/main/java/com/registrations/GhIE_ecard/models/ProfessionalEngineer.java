package com.registrations.GhIE_ecard.models;
import com.registrations.GhIE_ecard.enums.Institution;
import com.registrations.GhIE_ecard.enums.Regions;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "professional_engineers")
public class ProfessionalEngineer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private Regions region;

    @Column(name = "city_town", nullable = false)
    private String cityTown;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    private String linkedin;

    @Column(name = "membership_number", nullable = false, unique = true)
    private String membershipNumber;

    @Column(name = "membership_grade", nullable = false)
    private String membershipGrade;

    @Column(name = "branch_division", nullable = false)
    private String branchDivision;

    @Column(name = "current_employer")
    private String currentEmployer;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "employment_status")
    private String employmentStatus;

    @Column(name = "employment_sector")
    private String employmentSector;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "engineering_discipline", nullable = false)
    private String engineeringDiscipline;

    private String specialization;

    @Column(name = "industry_work")
    private String industryWork;

    private String qualification;

    @Enumerated(EnumType.STRING)
    private Institution institutions;

    @Column(name = "program_of_study")
    private String programOfStudy;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "registered_at", updatable = false)
    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();

        if (this.email != null) {
            this.email = this.email.toLowerCase().trim();
        }
    }

    // --- Constructors ---
    public ProfessionalEngineer() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Regions getRegion() { return region; }
    public void setRegion(Regions region) { this.region = region; }

    public String getCityTown() { return cityTown; }
    public void setCityTown(String cityTown) { this.cityTown = cityTown; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }

    public String getMembershipNumber() { return membershipNumber; }
    public void setMembershipNumber(String membershipNumber) { this.membershipNumber = membershipNumber; }

    public String getMembershipGrade() { return membershipGrade; }
    public void setMembershipGrade(String membershipGrade) { this.membershipGrade = membershipGrade; }

    public String getBranchDivision() { return branchDivision; }
    public void setBranchDivision(String branchDivision) { this.branchDivision = branchDivision; }

    public String getCurrentEmployer() { return currentEmployer; }
    public void setCurrentEmployer(String currentEmployer) { this.currentEmployer = currentEmployer; }

    public String getJobTitle() { return jobTitle; }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmploymentStatus() { return employmentStatus; }

    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public String getEmploymentSector() { return employmentSector; }

    public void setEmploymentSector(String employmentSector) { this.employmentSector = employmentSector; }

    public Integer getYearsExperience() { return yearsExperience; }

    public void setYearsExperience(Integer yearsExperience) { this.yearsExperience = yearsExperience; }

    public String getEngineeringDiscipline() { return engineeringDiscipline; }

    public void setEngineeringDiscipline(String engineeringDiscipline) { this.engineeringDiscipline = engineeringDiscipline; }

    public String getSpecialization() { return specialization; }

    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getIndustryWork() { return industryWork; }

    public void setIndustryWork(String industryWork) { this.industryWork = industryWork; }

    public String getQualification() { return qualification; }

    public void setQualification(String qualification) { this.qualification = qualification; }

    public Institution getInstitutions() { return institutions; }

    public void setInstitutions(Institution institutions) { this.institutions = institutions; }

    public String getProgramOfStudy() { return programOfStudy; }

    public void setProgramOfStudy(String programOfStudy) { this.programOfStudy = programOfStudy; }

    public String getCertifications() { return certifications; }

    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getComments() { return comments; }

    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
} // fixed brace during compilation typing

