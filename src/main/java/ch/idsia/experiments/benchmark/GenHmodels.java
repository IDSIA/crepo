package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.credal.linear.SeparateHalfspaceFactor;
import ch.idsia.crema.factor.credal.vertex.VertexFactor;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.experiments.Convert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenHmodels {
    public static void main(String[] args) throws IOException, InterruptedException {

        String prj_dir = ".";
        String vmodelFolder = prj_dir+"/networks/vmodel/";
        String hmodelFolder = prj_dir+"/networks/hmodel/";

        ArrayList<String> failed = new ArrayList<String>();
        List<String> files = getFiles(vmodelFolder);

        boolean rewrite = false;


        int i = 1;
        for(String vfile : files) {
            try {
                // Load the vmodel
                System.out.println("Processing "+(i++)+"/"+files.size());
                System.out.println(vfile);
                String name = vfile.substring(vfile.lastIndexOf("/") + 1).replace("vmodel", "hmodel");


                if (rewrite || !new File(hmodelFolder + "" + name).exists()) {

                    DAGModel vmodel = (DAGModel) IO.read(vfile);
                    // Convert the model
                    DAGModel hmodel = buildHmodel(vmodel);

                    // Save the hmodel
                    System.out.println("Saving " + hmodelFolder + "" + name);
                    IO.writeUAI(hmodel, hmodelFolder + "" + name);
                }
            }catch (Exception e){
                failed.add(vfile);
                System.out.println("ERROR: "+vfile);
                e.printStackTrace();

            }
        }

        System.out.println("\nFailed:\n===============");
        failed.forEach(System.out::println);


    }


    public static List<String> getFiles(String folder) throws IOException {
        return StreamSupport
                .stream(Files.newDirectoryStream(
                        Paths.get(folder),
                        path -> path.toString().endsWith(".uai") && path.toString().contains("vmodel-")
                ).spliterator(), false)
                .map(f -> f.toString())
                .collect(Collectors.toList());
    }


    public static DAGModel buildHmodel(DAGModel vmodel) throws IOException, InterruptedException {
        System.out.println("Converting "+vmodel.getVariables().length+" v-factors");
        return Convert.VmodelToHmodel(vmodel);
    }

}
