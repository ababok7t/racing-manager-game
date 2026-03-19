package repository;

import model.components.Component;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentRepository extends Repository<Component> {

    public Component save(Component component) {
        return super.save(component, component.getId());
    }

    public <T extends Component> List<T> findByType(Class<T> type) {
        return findAll().stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }
}