package at.ac.tuwien.infosys.viepep.database.inmemory.services;

import at.ac.tuwien.infosys.viepep.database.entities.docker.DockerImage;
import at.ac.tuwien.infosys.viepep.database.inmemory.database.InMemoryCacheImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by philippwaibel on 13/06/16.
 */
@Component
public class CacheDockerService {

    @Autowired
    private InMemoryCacheImpl inMemoryCache;

    @Value("${docker.repo.name}")
    private String repoName;
    @Value("${docker.image.name}")
    private String imageName;
    @Value("${docker.type.amount}")
    private Integer D = 3; // how many docker types
    @Value("${docker.type.config.amount}")
    private Integer C = 4; // different configurations of each docker type


    public void initializeDockerImages() {
        for (int c = 0; c < D; c++) {
            DockerImage dockerImage = parseByAppId("app" + c);
            inMemoryCache.addToDockerImageList(dockerImage);
        }
    }

    public DockerImage parseByImageName(String imageFullName) {
        if (imageFullName.contains("app0")) {
            return new DockerImage("app" + 0, repoName, imageName, 8090, 3000);
        }
        if (imageFullName.contains("app1")) {
            return new DockerImage("app" + 1, repoName, imageName, 8091, 3000);

        }
        if (imageFullName.contains("app2")) {
            return new DockerImage("app" + 2, repoName, imageName, 8092, 3000);
        }
        if (imageFullName.contains("app3")) {
            return new DockerImage("app" + 3, repoName, imageName, 8093, 3000);
        }
        return null;
    }


    public DockerImage parseByAppId(String appId) {
        if (appId.contains("app0")) {
            return parseByImageName("app0");
        }
        if (appId.contains("app1")) {
            return parseByImageName("app1");
        }
        if (appId.contains("app2")) {
            return parseByImageName("app2");
        }
        if (appId.contains("app3")) {
            return parseByImageName("app3");
        }
        return parseByImageName("app0");
    }

}
