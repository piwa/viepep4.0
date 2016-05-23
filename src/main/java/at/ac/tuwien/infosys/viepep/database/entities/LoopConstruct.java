package at.ac.tuwien.infosys.viepep.database.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;


/**
 * this class represents a LoopConstruct - a complex element in the model
 *
 * @author Waldemar Ankudin modified by Turgay Sahin
 */
@XmlRootElement(name = "LoopConstruct")
@Entity(name = "LoopConstruct")
@PrimaryKeyJoinColumn(name="id")
@Table(name="LoopConstructElement")
@DiscriminatorValue(value = "loop")
@Getter
@Setter
public class LoopConstruct extends Element {


    private int numberOfIterationsInWorstCase = 3;
    private int iterations = 0;

    public LoopConstruct(String n) {
        name = n;
        elements = new ArrayList<Element>();
    }

    public LoopConstruct() {
    }

    public long calculateQoS() {
        return elements.get(0).calculateQoS() * numberOfIterationsInWorstCase;
    }

    @Override
    public ProcessStep getLastExecutedElement() {
        return elements.get(elements.size() - 1).getLastExecutedElement();
    }

    @Override
    public String toString() {
        return "Loop{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", elements=" + elements +
                ", deadline=" + deadline +
                '}';
    }

}
