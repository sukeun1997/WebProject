package com.studyforyou_retry.modules.study;

import com.studyforyou_retry.modules.account.Account;
import com.studyforyou_retry.modules.account.UserAccount;
import com.studyforyou_retry.modules.tags.Tag;
import com.studyforyou_retry.modules.zones.Zone;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final ModelMapper modelMapper;

    public void createStudy(Account account, StudyForm studyForm) {
        Study study = modelMapper.map(studyForm, Study.class);
        study.addManagers(account);
        studyRepository.save(study);
    }


    public Study getStudyAllByManagers(Account account, String path) {

        Study study = this.getStudyAll(path);
        isNotManager(account, study);
        return study;
    }

    private void isNotManager(Account account, Study study) {
        if (!study.isManager(new UserAccount(account))) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    private void isExistStudy(Study study) {
        if (study == null) {
            throw new EntityNotFoundException("해당 스터디가 없습니다.");
        }
    }

    public void updateDescription(Study study, StudyDescriptionForm studyDescriptionForm) {
        study.updateDescription(studyDescriptionForm);
    }

    @Transactional(readOnly = true)
    public Study getStudyAll(String path) {
        Study study = studyRepository.findStudyWithAllByPath(path);
        isExistStudy(study);
        return study;
    }

    public void updateBanner(Study study, String image) {
        study.updateBanner(image);
    }

    public void enableBanner(Study study) {
        study.enableBanner();
    }

    public void disableBanner(Study study) {
        study.disableBanner();
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagersAndTagsByManagers(Account account, String path) {

        Study study = studyRepository.findStudyWithManagerAndTagsByPath(path);
        StudyAndManagerCheck(account, study);
        return study;
    }

    public void addTag(Study study, Tag tag) {
        study.addTags(tag);
    }

    public void removeTags(Study study, Tag tag) {
        study.removeTags(tag);
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagersAndZonesByManagers(Account account, String path) {
        Study study = studyRepository.findStudyWithManagerAndZonesByPath(path);
        StudyAndManagerCheck(account, study);
        return study;
    }

    private void StudyAndManagerCheck(Account account, Study study) {
        isExistStudy(study);
        isNotManager(account, study);
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagersByManagers(Account account, String path) {
        Study study = studyRepository.findStudyWithManagerByPath(path);
        StudyAndManagerCheck(account, study);
        return study;
    }

    @Transactional(readOnly = true)
    public Study getStudyWithManagers(String path) {
        Study study = studyRepository.findStudyWithManagerByPath(path);
        isExistStudy(study);
        return study;
    }


    public void addZones(Study study, Zone zone) {
        study.addZones(zone);
    }

    public void removeZones(Study study, Zone zone) {
        study.removeZones(zone);
    }

    public void publishStudy(Study study) {
        study.publish();
    }

    public void closeStudy(Study study) {
        study.close();
    }

    public void updatePath(Study study, String newPath) {
        study.updatePath(newPath);
    }

    public void updateTitle(Study study, String newTitle) {
        study.updateTitle(newTitle);

    }

    public void deleteStudy(Study study) {
        studyRepository.delete(study);
    }

    public void recruitStart(Study study) {
        study.recruitStart();
    }

    public void recruitStop(Study study) {
        study.recruitStop();
    }

    public boolean canRecruit(Study study) {
        return (study.getRecruitDateTime() == null || study.getRecruitDateTime().isBefore(LocalDateTime.now().minusHours(1))) && study.isPublished();
    }

    public void joinStudy(Study study, Account account) {
        study.joinStudy(account);
    }

    public void leaveStudy(Study study, Account account) {
        study.leaveStudy(account);
    }
}
