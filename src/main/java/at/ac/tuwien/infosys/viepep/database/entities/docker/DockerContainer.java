package at.ac.tuwien.infosys.viepep.database.entities.docker;

import at.ac.tuwien.infosys.viepep.database.entities.VirtualMachine;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
@Getter
@Setter
@Entity
@Table(name = "DockerContainer")
public class DockerContainer {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private DockerConfiguration containerConfiguration;

    @ManyToOne
    private DockerImage dockerImage;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<VirtualMachine> virtualMachines;

    private int amountOfPossibleInvocations;

    private long executionTime;
    private long deployTime;
    private long deployCost = 3;
    private String containerID;
    private String ipAddress;

    private long startupTime = 60000L;
    private boolean deployed;
    private Date startedAt;
    private boolean started;
    private boolean canBeTermianted;
    private Date toBeTerminatedAt;

    public DockerContainer() {
    }

    public DockerContainer(DockerImage dockerImage, DockerConfiguration containerConfiguration) {
        this.containerConfiguration = containerConfiguration;
        this.dockerImage = dockerImage;
        this.executionTime = 3000;
        this.amountOfPossibleInvocations = getInvocationAmount(dockerImage.getAppId());
        this.deployTime = 30000;
    }


    public DockerContainer(DockerImage dockerImag, long executionTime, DockerConfiguration containerConfiguration) {
        this.containerConfiguration = containerConfiguration;
        this.executionTime = executionTime;

        this.amountOfPossibleInvocations = getInvocationAmount(dockerImag.getAppId());
        this.deployTime = 30000;
        this.dockerImage = dockerImag;
    }

    private int getInvocationAmount(String appId) {
        int perCore = 0;
        switch ((int) containerConfiguration.cores) {
            case 1:
                perCore = (int) Math.round(containerConfiguration.cores * 80);
                break;
            case 2:
                perCore = (int) Math.round(containerConfiguration.cores * 90);
                break;
            case 4:
                perCore = (int) Math.round(containerConfiguration.cores * 100);
                break;
            case 8:
                perCore = (int) Math.round(containerConfiguration.cores * 110);
                break;
            default:
                perCore = (int) Math.round(containerConfiguration.cores * 100);
                break;
        }
        switch (appId) {
            case "app0":
                return (int) (perCore * 1.0);
            case "app1":
                return (int) (perCore * 1.0);
            case "app2":
                return (int) (perCore * 0.6);
            case "app3":
                return (int) (perCore * 1.0);
            default:
                return (int) (perCore * 1.0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DockerContainer)) return false;

        DockerContainer that = (DockerContainer) o;

        return this.dockerImage.getAppId().equals(that.getAppID());

    }

    @Override
    public int hashCode() {
        return this.dockerImage.getAppId().hashCode();
    }

    public String getName() {
        return containerConfiguration.name() + "_" + this.dockerImage.getAppId();
    }

    public String getAppID() {
        return this.dockerImage.getAppId();
    }

    public void addVirtualMachine(VirtualMachine virtualMachine) {
        if (virtualMachines == null) {
            virtualMachines = new ArrayList<>();
        }
        virtualMachines.add(virtualMachine);
    }

    public void terminate() {
        this.setCanBeTermianted(false);
        this.setStarted(false);
        this.setStartedAt(null);
        this.setToBeTerminatedAt(null);
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String startString = startedAt == null ? "NOT_YET" : simpleDateFormat.format(startedAt);
        String toBeTerminatedAtString = toBeTerminatedAt == null ? "NOT_YET" : simpleDateFormat.format(toBeTerminatedAt);
        return "DockerContainer{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", appId=" + this.dockerImage.getAppId() +
                ", startedAt=" + startString +
                ", terminateAt=" + toBeTerminatedAtString +
                ", ip adress=" + ipAddress +
                '}';
    }

    public String getURI() {
        return "http://" + this.ipAddress;
    }
}
