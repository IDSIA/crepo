package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.credal.vertex.VertexFactor;
import ch.idsia.crema.model.graphical.SparseModel;
import ch.idsia.crema.model.io.bif.XMLBIFParser;
import ch.idsia.crema.utility.RandomUtil;
import ch.idsia.crema.utility.hull.LPConvexHull;
import com.google.common.collect.Lists;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenVmodels {

    static int numDecimals = 3;


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        String prj_dir = ".";
        String preciseFolder = prj_dir+"/networks/precise/";
        String vmodelFolder = prj_dir+"/networks/vmodel/";
        int[] nVert = {2, 4, 6};


        List<String> files = getFiles(preciseFolder);


        int i = 1;
        for(String bnetFile : files) {
            System.out.println("Processing "+(i++)+"/"+files.size());
            System.out.println(bnetFile);
            SparseModel bnet = readBnet(bnetFile);
            System.out.println("Reading "+bnetFile);
            for(int nV : nVert) {
                // get the name of the output uai file
                String name = getNameFrom(bnetFile, nV);

                // Set always the same seed for a same file, regardless of the order
                RandomUtil.setRandomSeed(name.hashCode());

                // generate the new model and write it
                SparseModel vmodel = buildVmodel(bnet, nV);
                System.out.println("Saving " + vmodelFolder + "" + name);
                IO.write(vmodel, vmodelFolder + "" + name);
            }


        }
        System.out.println("Done");

    }

    public static String getNameFrom(String bnetFile, int nVert) {
        String argsStr = bnetFile.substring(bnetFile.lastIndexOf("bnet")+4, bnetFile.lastIndexOf("-"));
        String indexNet = bnetFile.substring(bnetFile.lastIndexOf("-"), bnetFile.lastIndexOf("."));
        return "vmodel"+argsStr+"_nV"+nVert+indexNet+".uai";
    }

    public static List<String> getFiles(String folder) throws IOException {
        return StreamSupport
                    .stream(Files.newDirectoryStream(
                            Paths.get(folder),
                        path -> path.toString().endsWith(".xml")
                    ).spliterator(), false)
                    .map(f -> f.toString())
                    .collect(Collectors.toList());
    }

    public static SparseModel readBnet(String bnetFile) throws SAXException, IOException, ParserConfigurationException {
        XMLBIFParser parser = new XMLBIFParser();
        FileInputStream fio = new FileInputStream(bnetFile);
        return parser.parse(fio);
    }

    public static SparseModel buildVmodel(SparseModel bnet, int nVert) {
        // generate an credal network with the same structure but without factor
        SparseModel vmodel = new SparseModel();

        for(int x : bnet.getVariables()) {
            int cardX = Math.max(bnet.getDomain(x).getCardinality(x), 2);
            vmodel.addVariable(cardX);
        }
        for(int x : bnet.getVariables()) {
            // add the same parents
            vmodel.addParents(x, bnet.getParents(x));
            // generate a random
            if(vmodel.getParents(x).length == 0)
                vmodel.setFactor(x, VertexFactor.random(vmodel.getDomain(x), nVert, numDecimals, true));
            else
                vmodel.setFactor(x, VertexFactor.random(vmodel.getDomain(x), vmodel.getDomain(vmodel.getParents(x)), nVert, numDecimals, false));

        }
        return vmodel;
    }
}
