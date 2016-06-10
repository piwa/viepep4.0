package at.ac.tuwien.infosys.viepep.connectors.impl;

import at.ac.tuwien.infosys.viepep.connectors.ViePEPAwsClientService;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ViePEPAwsClientServiceImpl implements ViePEPAwsClientService {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(ViePEPAwsClientServiceImpl.class);
    private static String propertyFile;

    private static String AWS_ACCESS_KEY_ID = "CHANGE_ME";
    private static String AWS_ACCESS_KEY = "CHANGE_ME";
    private static String AWS_DEFAULT_IMAGE = "CHANGE_ME";
    private static String AWS_DEFAULT_SUBNET = "CHANGE_ME";

    private static AmazonEC2 ec2;
    private static ViePEPAwsClientServiceImpl instance;

    public static ViePEPAwsClientServiceImpl getInstance() {
        if (instance == null) {
            instance = new ViePEPAwsClientServiceImpl();
        }
        return instance;
    }

    private boolean enabled = false;

    private ViePEPAwsClientServiceImpl() {
        init();
    }

    private void loadSettings() {
        Properties prop = new Properties();
        try {
            propertyFile = System.getenv("OPENSTACK_PROPERTY_FILE");
            if (propertyFile != null) {
                prop.load(new FileInputStream(propertyFile));
            } else {
                propertyFile = "cloud-config/viepep4.0.properties";
                prop.load(getClass().getClassLoader().getResourceAsStream(propertyFile));
            }

            AWS_ACCESS_KEY_ID = prop.getProperty("AWS_ACCESS_KEY_ID");
            AWS_ACCESS_KEY = prop.getProperty("AWS_ACCESS_KEY");
            AWS_DEFAULT_IMAGE = prop.getProperty("AWS_DEFAULT_IMAGE");
            AWS_DEFAULT_SUBNET = prop.getProperty("AWS_DEFAULT_SUBNET");

            if (AWS_ACCESS_KEY_ID.isEmpty() || AWS_ACCESS_KEY.isEmpty()) {
                throw new Exception("Could not find variables ");
            }
        } catch (Exception e) {
            this.enabled = false;
        }
        LOG.info("\n---------------------------------------------------------");
        LOG.info("\n------------aws properties loaded------------------------");
        LOG.info("\n---------------------------------------------------------");
    }

    public void init() {
        loadSettings();

        AWSCredentials credentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return AWS_ACCESS_KEY_ID;
            }

            @Override
            public String getAWSSecretKey() {
                return AWS_ACCESS_KEY;
            }
        };

        ec2 = new AmazonEC2Client(credentials);
        Region defaultRegion = Region.getRegion(Regions.US_EAST_1);
        ec2.setRegion(defaultRegion);

        enabled = true;
        LOG.info("autostartPEP-aws file loaded");
    }

    @Override
    public void terminateInstanceByID(String instanceId) {
        TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest();
        terminateRequest.getInstanceIds().add(instanceId);
        ec2.terminateInstances(terminateRequest);
    }

    @Override
    public boolean terminateInstanceByIP(String localeAddress) {
        terminateInstanceByID(getVMIdByIP(localeAddress));
        return waitForStatus(getVMIdByIP(localeAddress), "terminated", 100);
    }

    @Override
    public List<String> getServerIpList() {
        List<String> result = new ArrayList<>();
        DescribeInstancesResult r = ec2.describeInstances();
        for (Reservation reservations : r.getReservations()) {
            List<Instance> instances = reservations.getInstances();
            for (Instance instance : instances) {
                if (!instance.getState().getName().equals("terminated")) {
                    result.add(instance.getPublicIpAddress());
                }
            }
        }
        return result;
    }

    @Override
    public String startNewVM(String name, String flavorName, String serviceName, String location) {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

        runInstancesRequest.withImageId(AWS_DEFAULT_IMAGE)
                .withInstanceType(flavorName)
                .withMinCount(1)
                .withMaxCount(1)
                .withSubnetId(AWS_DEFAULT_SUBNET);

        ec2.runInstances(runInstancesRequest);

        String instanceId = null;
        DescribeInstancesResult result = ec2.describeInstances();
        for (Reservation r : result.getReservations()) {
            List<Instance> instances = r.getInstances();
            for (Instance ii : instances) {
                if (ii.getState().getName().equals("pending")) {
                    instanceId = ii.getInstanceId();
                }
            }
        }

        boolean isWaiting = true;
        while (isWaiting) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DescribeInstancesResult r = ec2.describeInstances();
            for (Reservation rr : r.getReservations()) {
                List<Instance> instances = rr.getInstances();
                for (Instance instance : instances) {
                    LOG.info("AWS instance " + instance.getInstanceId() + " and public IP " + instance.getPublicIpAddress() + " was started");
                    if (instance.getState().getName().equals("running") && instance.getInstanceId().equals(instanceId)) {
                        return instance.getPublicIpAddress();
                    }
                }
            }
        }
        return null;
    }

    private Boolean waitForStatus(String instanceID, String status, Integer limit) {

        boolean isWaiting = true;
        while (isWaiting && limit > 0) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (checkVMStatebyID(instanceID, status)) {
                return true;
            }
        }

        return false;
    }

    public String getVMIdByIP(String ip) {
        DescribeInstancesResult r = ec2.describeInstances();
        for (Reservation reservations : r.getReservations()) {
            List<Instance> instances = reservations.getInstances();
            for (Instance instance : instances) {
                if (instance.getState().getName().equals("running") && instance.getPublicIpAddress().equals(ip)) {
                    return instance.getInstanceId();
                }
            }
        }
        throw new IllegalArgumentException(String.format("VM with IP %s not found", ip));
    }

    @Override
    public boolean isVMRunning(String ip) {
        return checkVMStatebyIP(ip, "running");
    }

    private boolean checkVMStatebyIP(String ip, String status) {
        DescribeInstancesResult r = ec2.describeInstances();
        for (Reservation reservations : r.getReservations()) {
            List<Instance> instances = reservations.getInstances();
            for (Instance instance : instances) {
                if (instance.getState().getName().equals(status) && instance.getPublicIpAddress().equals(ip)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkVMStatebyID(String id, String status) {
        DescribeInstancesResult r = ec2.describeInstances();
        for (Reservation reservations : r.getReservations()) {
            List<Instance> instances = reservations.getInstances();
            for (Instance instance : instances) {
                if (instance.getState().getName().equals(status) && instance.getInstanceId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }


}
