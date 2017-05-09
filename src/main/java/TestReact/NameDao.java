package TestReact;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NameDao {
    private List<String> names = new ArrayList<>();

    public List<String> getAll() {
        return names;
    }

    public void add(String name) {
        names.add(name);
    }
}
