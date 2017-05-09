package TestReact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NameApi {
    @Autowired
    private NameDao nameDao;

    @RequestMapping("/names")
    List<String> getNames() {
        return nameDao.getAll();
    }

    @RequestMapping(value = "/name/{name}", method = RequestMethod.GET)
    void addName(@PathVariable("name") String name) {
        nameDao.add(name);
    }
}
