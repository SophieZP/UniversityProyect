package SophieZP.demo.service;

import SophieZP.demo.entity.Subject;
import SophieZP.demo.entity.User;
import SophieZP.demo.repository.SubjectRepository;
import SophieZP.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    //Logic: Save a Subject for a especific user
    public Subject createSubject(long userId, Subject subject){
        User user = userRepository.findById(userId).orElse(null);
        if (user != null){
            subject.setUser(user); //We asign the user to the Subject
            return subjectRepository.save(subject);
        }
    return null;
    }

    public List<Subject> getSubjectByUserId(Long userId){
        return subjectRepository.findByUserId(userId);
    }
}

