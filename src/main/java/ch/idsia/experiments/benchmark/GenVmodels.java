package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.convert.HalfspaceToVertex;
import ch.idsia.crema.factor.credal.linear.SeparateHalfspaceFactor;
import ch.idsia.crema.factor.credal.vertex.VertexFactor;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.model.io.bif.XMLBIFParser;
import ch.idsia.crema.utility.RandomUtil;
import ch.idsia.crema.utility.hull.LPConvexHull;
import ch.idsia.experiments.Convert;
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
        //int[] nVert = {2, 4, 6};
        int[] nVert = {4};



        List<String> files = getFiles(preciseFolder);




        int i = 1;
        for(String bnetFile : files) {

            System.out.println(bnetFile);
            //bnetFile = "./networks/precise/bnet-mult_n4_mID2_mD6_mV4-3.xml";

            System.out.println("Processing "+(i++)+"/"+files.size());
            System.out.println(bnetFile);
            DAGModel bnet = readBnet(bnetFile);
            System.out.println("Reading "+bnetFile);
            for(int nV : nVert) {
                // get the name of the output uai file
                String name = getNameFrom(bnetFile, nV);

                // Set always the same seed for a same file, regardless of the order
                RandomUtil.setRandomSeed(name.hashCode());

                // generate the new model and write it
                DAGModel vmodel = buildVmodel(bnet, nV);
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

    public static DAGModel readBnet(String bnetFile) throws SAXException, IOException, ParserConfigurationException {
        XMLBIFParser parser = new XMLBIFParser();
        FileInputStream fio = new FileInputStream(bnetFile);
        return (DAGModel) parser.parse(fio);
    }

    public static DAGModel buildVmodel(DAGModel bnet, int nVert) {
        // generate an credal network with the same structure but without factor
        DAGModel vmodel = new DAGModel();

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

    public static void setRandomVFactor(DAGModel vmodel, int x, int nVert) {
        VertexFactor vf = null;
        boolean repeat = true;
        int i = 0;

        do {
            i++;
            if(vmodel.getParents(x).length == 0)
                vf = VertexFactor.random(vmodel.getDomain(x), nVert, numDecimals, true);
            else
                vf = VertexFactor.random(vmodel.getDomain(x), vmodel.getDomain(vmodel.getParents(x)), nVert, numDecimals, false);

            // Check if it can be converted V -> H and H --> V
            try {
                SeparateHalfspaceFactor hf = Convert.vertexToHspace(vf);
                VertexFactor vf2 = new HalfspaceToVertex().apply(hf, x);
                if(vf2 == null)
                    throw  new IllegalStateException();
                else
                    repeat = false;
            }catch (Exception e){
                System.out.print(".");
                repeat = true;
            }
        }while (repeat);


        System.out.println(i+" repetitions");



        vmodel.setFactor(x, vf);



    }
}
