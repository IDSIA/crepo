package ch.idsia.experiments;

import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.model.io.bif.XMLBIFParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;

public class Example {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("example class");

        XMLBIFParser parser = new XMLBIFParser();
		FileInputStream fio = new FileInputStream("networks/precise/bnet-mult_n4_mID2_mD6_mV4-1.xml");

        DAGModel<BayesianFactor> model = (DAGModel<BayesianFactor>) parser.parse(fio);

        System.out.println(model);
    }
}
