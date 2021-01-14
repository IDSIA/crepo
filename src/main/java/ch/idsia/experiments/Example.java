package ch.idsia.experiments;
import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.model.graphical.SparseModel;
import ch.idsia.crema.model.io.bif.XMLBIFParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Example {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("example class");

        XMLBIFParser parser = new XMLBIFParser();
		FileInputStream fio = new FileInputStream("networks/Benchmark-long1.xml");

        SparseModel<BayesianFactor> model = parser.parse(fio);

        System.out.println(model);


    }
}
