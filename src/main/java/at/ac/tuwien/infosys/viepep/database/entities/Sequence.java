package at.ac.tuwien.infosys.viepep.database.entities;


import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;


/**
 * @author Waldemar Ankudin modified by Turgay Sahin and Mathieu Muench
 */

@XmlRootElement(name = "Sequence")
@Entity(name = "Sequence")
public class Sequence extends Element {


    /**
     * sequence element which contains the name and the elements in a sequence
     *
     * @param n String
     */
    public Sequence(String n) {
        name = n;
        elements = new ArrayList<Element>();
    }

    public Sequence() {
        elements = new ArrayList<>();
    }

    public long calculateQoS() {

        long executionTime = 0;
        for (Element element : elements) {
            executionTime += element.calculateQoS();
        }
        return executionTime;
    }

    @Override
    public ProcessStep getLastExecutedElement() {
        return elements.get(elements.size() - 1).getLastExecutedElement();
    }
}