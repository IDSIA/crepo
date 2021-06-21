package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.core.Strides;
import ch.idsia.crema.factor.bayesian.BayesianFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactorUtilities;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.model.io.bif.XMLBIFParser;
import ch.idsia.crema.utility.RandomUtil;
import ch.idsia.experiments.Convert;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenVmodels {

    static int numDecimals = 3;


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InterruptedException {

        System.out.println("Generating Vmodels");
        String prj_dir = ".";
        String preciseFolder = prj_dir+"/networks/precise/";
        String vmodelFolder = prj_dir+"/networks/vmodel/";
        int[] nVert = {2, 4, 6};
        //int[] nVert = {4};
        boolean rewrite = false;

        List<String> files = getFiles(preciseFolder);

        int i = 1;
        for(String bnetFile : files) {
            System.out.println(bnetFile);
            //bnetFile = "./networks/precise/bnet-mult_n4_mID2_mD6_mV4-3.xml";

            System.out.println("Processing "+(i++)+"/"+files.size());
            System.out.println(bnetFile);
            DAGModel<BayesianFactor> bnet = readBnet(bnetFile);
            System.out.println("Reading "+bnetFile);
            for(int nV : nVert) {
                // get the name of the output uai file
                String name = getNameFrom(bnetFile, nV);

                if (rewrite || !new File(vmodelFolder + name).exists()) {
                    // Set always the same seed for a same file, regardless of the order
                    RandomUtil.setRandomSeed(name.hashCode());

                    // generate the new model and write it
                    DAGModel<VertexFactor> vmodel = buildVmodel(bnet, nV);
                    System.out.println("\nSaving " + vmodelFolder + "" + name);
                    IO.write(vmodel, vmodelFolder + "" + name);
                }
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
                    .map(Path::toString)
                    .collect(Collectors.toList());
    }

    public static DAGModel<BayesianFactor> readBnet(String bnetFile) throws SAXException, IOException, ParserConfigurationException {
        XMLBIFParser parser = new XMLBIFParser();
        FileInputStream fio = new FileInputStream(bnetFile);
        return (DAGModel<BayesianFactor>) parser.parse(fio);
    }

    public static DAGModel<VertexFactor> buildVmodel(DAGModel<BayesianFactor> bnet, int nVert) throws IOException, InterruptedException {
        // generate an credal network with the same structure but without factor
        DAGModel<VertexFactor> vmodel = new DAGModel<>();

        for(int x : bnet.getVariables()) {
            int cardX = Math.max(bnet.getDomain(x).getCardinality(x), 2);
            vmodel.addVariable(cardX);
        }
        for(int x : bnet.getVariables()) {
            // add the same parents
            vmodel.addParents(x, bnet.getParents(x));
            // generate a random
            setRandomVFactor(vmodel, x, nVert);
        }
        return vmodel;
    }

    public static void setRandomVFactor(DAGModel<VertexFactor> vmodel, int x, int nVert) {
        VertexFactor vf;
        if(vmodel.getParents(x).length == 0)
            vf = random(vmodel.getDomain(x), Strides.empty(), nVert, numDecimals, true);
        else
            vf = random(vmodel.getDomain(x), vmodel.getDomain(vmodel.getParents(x)), nVert, numDecimals, true);

        vmodel.setFactor(x, vf);
    }

    public static VertexFactor random(Strides leftDomain, Strides rightDomain, int nVert, int num_decimals, boolean zero_allowed) {

        // array for storing the vertices
        final int combinations = rightDomain.getCombinations();
        double[][][] data = new double[combinations][][];
        VertexFactor[] vertices = new VertexFactor[data.length];

        // generate independently for each parent
        for (int i = 0; i < combinations; i++) {
            boolean valid = false;
            do {
                System.out.print(".");
                VertexFactor vfi = VertexFactorUtilities.random(leftDomain, nVert);
                if (Convert.isConvertible(vfi, leftDomain.getVariables()[0])) {
                    vertices[i] = vfi;
                    valid = true;
                }
            } while (!valid);
           if(i < combinations-1)
               System.out.print("/");
           else
               System.out.print("|");


        }

        // build final factor
        return VertexFactorUtilities.mergeVertices(vertices);
    }
}
