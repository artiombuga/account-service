package account.utiliy;

import account.model.Group;
import account.repository.GroupRepository;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {
    private final GroupRepository groupRepository;

    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new Group("ROLE_ADMINISTRATOR"));
            groupRepository.save(new Group("ROLE_USER"));
            groupRepository.save(new Group("ROLE_ACCOUNTANT"));
            groupRepository.save(new Group("ROLE_AUDITOR"));
        } catch (Exception e) {
            System.out.println("Roles already exist");;
        }

    }
}
