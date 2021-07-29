package ch.idsia.experiments.benchmark;

import ch.idsia.crema.IO;
import ch.idsia.crema.factor.credal.linear.separate.SeparateHalfspaceFactor;
import ch.idsia.crema.factor.credal.vertex.separate.VertexFactor;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.experiments.Convert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenHmodels {
    public static void main(String[] args) throws IOException, InterruptedException {

        String prj_dir = "../../";
        String vmodelFolder = prj_dir+"/networks/vmodel/";
        String hmodelFolder = prj_dir+"/networks/hmodel/";

        List<String> failed = new ArrayList<>();
        List<String> files = getFiles(vmodelFolder);

        boolean rewrite = true;

        int i = 1;
        for(String vfile : files) {
            try {
                // Load the vmodel
                System.out.println("Processing " + (i++) + "/" + files.size());
                System.out.println(vfile);
                String name = vfile.substring(vfile.lastIndexOf("/") + 1).replace("vmodel", "hmodel");

                final File file = new File(hmodelFolder + "" + name);
                if (rewrite || !file.exists()) {
                    // TODO: assuming that "vmodel" is for VertexFactor models, and "hmodel" are for SeparateHalfspaceFactor
                    DAGModel<VertexFactor> vmodel = IO.read(vfile);
                    // Convert the model
                    DAGModel<SeparateHalfspaceFactor> hmodel = buildHmodel(vmodel);

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
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    public static DAGModel<SeparateHalfspaceFactor> buildHmodel(DAGModel<VertexFactor> vmodel) throws IOException, InterruptedException {
        System.out.println("Converting " + vmodel.getVariables().length + " v-factors");
        return Convert.VmodelToHmodel(vmodel);
    }

}
