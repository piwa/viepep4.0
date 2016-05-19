package at.ac.tuwien.infosys.viepep.connectors.impl;

import java.util.Date;

/**
 * User: bonomat
 * Date: 6/11/13
 */
public class StartBackendVMCommand {


    private static String generateCommand(String serviceID, String propertyFilePath) {
        return String.format("#!/bin/bash\n" +
                "#generated=" + new Date().toString() + "\n" +
//                "export JAVA_HOME/usr/lib/jvm/java-7-oracle/\n" +
//                "export TOMCAT_HOME=/home/ubuntu/servers/apache-tomcat-7.0.53/\n" +
                "#export path for backend properties\n" +
                "export BACKEND_PROPERTY_FILE=" + propertyFilePath + "\n" +
                "#export local ip address\n" +
                "export LOCAL_IP=$(/bin/hostname -i)\n" +
                "export PRODUCTION=true\n" +
                "echo deploying service :" + serviceID + "\n" +
//                "cp services/" + serviceID + " $TOMCAT_HOME/webapps/\n" +
                "\n" +
                "cd /home/ubuntu/viepep/backendVM\n" +
                "\n" +
                "if [ $PRODUCTION ]\n" +
                "then\n" +
                "\t ./run.sh \n" +
                "else\n" +
                "\t ./start.sh \n" +
                "fi\n");
    }

    public static String generateBackendConfig(String ip, String serviceID, int bpmsPort, String propertyFilePath) {
        String autostartPep = generateCommand(serviceID, propertyFilePath);
        return "#!/bin/bash\n" +
                "\n" +
                "echo \"-------------------\"\n" +
                "echo \"initializing viepep\"\n" +
                "echo \"-------------------\"\n" +
                "\n" +
                "rm " + propertyFilePath + "\n" +
                "echo \"-------------------\"\n" +
                "echo \"exporting settings: \"\n" +
                "echo \"-------------------\n" +
                "BPMS_IP=" + ip + "\n" +
                "BPMS_MQ_PORT=" + bpmsPort + "\n" +
                "PRODUCTION=true\n" +
                "RUN_LOCALLY=false\n" +
                "\">" + propertyFilePath +
                "echo \"LOCAL_IP=\"$(/bin/hostname -I) >>" + propertyFilePath + "\n" +
                "\n" +
                "echo \"-------------------\"\n" +
                "echo \"starting viepep with screen command\"\n" +
                "echo \"-------------------\"\n" +
                "\n" +
                "echo '" + autostartPep + "' > /home/ubuntu/startBackend.sh" + "\n" +
                "chmod a+x /home/ubuntu/startBackend.sh\n" +
                "echo \"screen -dmS backend bash -c ' /home/ubuntu/startBackend.sh' " +
                "\" > " +
                "/home/ubuntu/startBackendVM.sh\n" +
                "\n" +
                "chmod a+x /home/ubuntu/startBackendVM.sh\n" +
                "\n" +
                "su ubuntu -c \"/home/ubuntu/startBackendVM.sh\" & \n" +
                "echo running";
    }
}
