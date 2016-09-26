package at.ac.tuwien.infosys.viepep.database.entities;

import lombok.Getter;

/**
 * Created by Philipp Hoenisch on 6/16/14.
 */
@Getter
public enum ServiceType {

    Task1("task1", 50, 450, 1000 * 40, 2, false),
    Task2("task2", 75, 720, 1000 * 80, 4, false),
    Task3("task3", 75, 720, 1000 * 120, 6, true),
    Task4("task4", 100, 960, 1000 * 40, 2, false),
    Task5("task5", 120, 1150, 1000 * 100, 4, false),
    Task6("task6", 125, 1150, 1000 * 20, 6, true),
    Task7("task7", 150, 1440, 1000 * 40, 2, false),
    Task8("task8", 175, 1680, 1000 * 20, 4, false),
    Task9("task9", 250, 2400, 1000 * 60, 6, true),
    Task10("task10", 333, 3200, 1000 * 30, 2, false);
    ServiceType(String name, double cpuLoad, double memoryInByte, long makeSpan, double dataToTransfer, boolean onlyInternal) {
        this.name = name;
        this.cpuLoad = cpuLoad;
        this.memory = memoryInByte;
        this.makeSpan = makeSpan;
        this.dataToTransfer = dataToTransfer;
        this.onlyInternal = onlyInternal;
    }

    final double cpuLoad;
    final double memory;
    final long makeSpan;
    final String name;
    final double dataToTransfer;
    final boolean onlyInternal;

    public static ServiceType fromString(String serviceType) {
        switch (serviceType) {
            case "task1":
                return Task1;
            case "task2":
                return Task2;
            case "task3":
                return Task3;
            case "task4":
                return Task4;
            case "task5":
                return Task5;
            case "task6":
                return Task6;
            case "task7":
                return Task7;
            case "task8":
                return Task8;
            case "task9":
                return Task9;
            case "task10":
                return Task10;
            default:
                return Task1;
        }
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public long getMakeSpan() {
        return makeSpan;
    }

    public String getName() {
        return name;
    }

    public double getDataToTransfer() {
        return dataToTransfer;
    }

    public boolean isOnlyInternal() {
        return onlyInternal;
    }
}
