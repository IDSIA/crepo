package ch.idsia.experiments;

import ch.idsia.crema.factor.convert.HalfspaceToVertex;
import ch.idsia.crema.factor.credal.linear.SeparateHalfspaceFactor;
import ch.idsia.crema.factor.credal.vertex.VertexFactor;
import ch.idsia.crema.core.Strides;
import ch.idsia.crema.model.graphical.DAGModel;
import ch.idsia.crema.utility.ArraysUtil;
import ch.idsia.util;
import org.apache.commons.math3.optim.linear.Relationship;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Convert {

    public static String libPath = "./lib/";


    public static SeparateHalfspaceFactor vertexToHspace(VertexFactor factor) throws IOException, InterruptedException {


        SeparateHalfspaceFactor HF = new SeparateHalfspaceFactor(factor.getDataDomain(), factor.getSeparatingDomain());

        for(int i=0; i<factor.getSeparatingDomain().getCombinations(); i++) {
            VertexFactor VFi = new VertexFactor(factor.getDataDomain(), Strides.empty(), new double[][][]{factor.getData()[i]});
            SeparateHalfspaceFactor HFi = Convert.margVertexToHspace(VFi);
            HF.setLinearProblemAt(i, HFi.getLinearProblemAt(0));
        }

        return HF;

    }


    public static SeparateHalfspaceFactor margVertexToHspace(VertexFactor factor) throws IOException, InterruptedException {


        if(factor.getSeparatingDomain().getSize()>0)
            throw new IllegalArgumentException("input factor must have no parents");

        String polco_jar = libPath+"/polco.jar";

        if(!new File(polco_jar).exists())
            throw new InvalidPathException(polco_jar, "Polco jar not found");



        int numVert = factor.getData()[0].length;
        int numDim = factor.getDataDomain().getCombinations();

        // only because of the current implementation
        if(numDim>numVert) {
            if (numVert == 2) {
                return lineVertexToHspace(factor);
            } else {
                throw new IllegalArgumentException("The number of vertices cannot be lower than the cardinality");

            }
        }

        // build .ext file
        String extContent = "V-representation\n" +
                "begin\n"+(numVert)+" "+(numDim+1)+" real\n";

        for(double[] v : factor.getData()[0]){
            extContent += "1"+ DoubleStream.of(v)
                    .mapToObj(d -> " "+Double.toString(d))
                    .collect(Collectors.joining())+"\n";
        }

        extContent +="end\n";
        extContent += "hull\n";
        extContent += "incidence\n";


        File extFile = File.createTempFile("factor", ".ext");
        FileWriter writer = new FileWriter(extFile);
        writer.write(extContent);
        writer.close();

        // Create output file
        File outFile = File.createTempFile("factor", ".txt");

        // invoke commandline
        String cmd = "java -jar "+polco_jar +
                " -kind cdd -in "+extFile.getAbsolutePath()+
                " -out text "+outFile.getAbsolutePath();


        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();

        if(p.exitValue()>0)
            throw new RuntimeException("problem with bash command.");


        // read generated txt file and build the inequalities
        SeparateHalfspaceFactor hf = new SeparateHalfspaceFactor(factor.getDomain(), Strides.empty());

        //Ax <= b
        for(String l : new BufferedReader(new FileReader(outFile)).lines().toArray(String[]::new)){
            String[] vals = l.split("\t");

            double b = util.parseDouble(vals[0]);
            double[] A = new double[vals.length-1];

            for (int i = 1; i < vals.length; i++) {
                A[i - 1] = util.parseDouble(vals[i]);
                if (A[i - 1] != 0.0)
                    A[i - 1] *= -1;
            }

            hf.addConstraint(A, Relationship.LEQ, b);

        }

        // Non-negativ constraints
        for(int i=0; i<numDim; i++){
            double[] A = new double[numDim];
            A[i] = 1;
            hf.addConstraint(A, Relationship.GEQ, 0.0);
        }

        // normalization constraint
        hf.addConstraint(
                IntStream.range(0,numDim).mapToDouble(i -> 1.0).toArray(),
                Relationship.EQ,
                1.0
        );

        return hf;

    }

    public static double[] lstsq(double[][] points) throws IOException, InterruptedException {

        // Check python path
        String py_script = libPath+"lstsq.py";

        if(!new File(py_script).exists())
            throw new InvalidPathException(py_script, "Python script not found");

        String cmd = "python " + py_script;

        for (double[] p : points) {
            for (int i = 0; i < p.length; i++)
                cmd += " " + p[i];
        }

        Process p = Runtime.getRuntime().exec(cmd);
        p.waitFor();

        if (p.exitValue() > 0)
            throw new RuntimeException("problem with bash command.");

        String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();

        // b -A
        return Stream.of(output.split("\t")).mapToDouble(s -> Double.parseDouble(s)).toArray();
    }

    public static SeparateHalfspaceFactor lineVertexToHspace(VertexFactor factor) throws IOException, InterruptedException {


        if(factor.getSeparatingDomain().getSize()>0)
            throw new IllegalArgumentException("input factor must have no parents");


        // Check number of vertices
        int numVert = factor.getData()[0].length;
        int numDim = factor.getDataDomain().getCombinations();

        if(numVert>2)
            throw new IllegalArgumentException("Too many vertices");

        // Get the constraints
        List out = getLineConstMatrix(factor.getData()[0]);
        double[] b = (double[]) out.get(0);
        double[][] A = (double[][]) out.get(1);

        // Define the H-factor
        SeparateHalfspaceFactor hf = new SeparateHalfspaceFactor(factor.getDomain(), Strides.empty());

        for(int i=0; i<A.length; i++)
            hf.addConstraint(A[i], Relationship.EQ, b[i]);


        // Bound constraints
        for(int i=0; i<numDim; i++){
            double[] Ai = new double[numDim];
            Ai[i] = 1;
            double[] bounds = {factor.getData()[0][0][i],factor.getData()[0][1][i]};
            if(bounds[0] != bounds[1]) {
                hf.addConstraint(Ai, Relationship.GEQ, Math.min(bounds[0], bounds[1]));
                hf.addConstraint(Ai, Relationship.LEQ, Math.max(bounds[0], bounds[1]));
            }else{
                hf.addConstraint(Ai, Relationship.EQ, bounds[0]);
            }
        }

        // normalization constraint
        hf.addConstraint(
                IntStream.range(0,numDim).mapToDouble(i -> 1.0).toArray(),
                Relationship.EQ,
                1.0
        );


        return hf;
    }


    private static List getLineConstMatrix(double[][] vert) throws IOException, InterruptedException {

        // remove constant dimensions
        int[] nonConst =
                IntStream.range(0, vert[0].length)
                        .filter(i-> vert[0][i] != vert[1][i])
                        .toArray();

        double[][] V =  ArraysUtil.sliceColumns(vert, nonConst);

        int dim = V[0].length;
        int numConst = dim-2;

        double[][] A = new double[numConst][dim];
        double[] b = new double[numConst];


        for(int s=0; s<numConst; s++) {
            double[] vals =  lstsq(ArraysUtil.sliceColumns(V, s, s + 1));
            b[s] = vals[0];
            double[] A_s = IntStream.range(1,vals.length).mapToDouble(i-> -1*vals[i]).toArray();
            for(int i=0; i<A_s.length; i++)
                A[s][i+s] = A_s[i];
        }


        double[][] Aexp = new double[numConst][vert[0].length];
        for(int i=0; i<A.length; i++){
            for(int j=0; j<dim; j++){
                Aexp[i][nonConst[j]] = A[i][j];
            }
        }

        return Arrays.asList(b, Aexp);
    }

    public static DAGModel VmodelToHmodel(DAGModel vmodel) throws IOException, InterruptedException {

        DAGModel hmodel = (DAGModel) vmodel.copy();
        int[] variables = vmodel.getVariables();

        for (int x : variables) {
            SeparateHalfspaceFactor hf = Convert.vertexToHspace((VertexFactor) vmodel.getFactor(x));
            hmodel.setFactor(x, hf);
        }
        return hmodel;
    }

    public static DAGModel HmodelToVmodel(DAGModel hmodel) throws IOException, InterruptedException {

        DAGModel vmodel = (DAGModel) hmodel.copy();
        int[] variables = hmodel.getVariables();

        for (int x : variables) {
            VertexFactor vf = (new HalfspaceToVertex()).apply((SeparateHalfspaceFactor) hmodel.getFactor(x), x);
            vmodel.setFactor(x, vf);
        }
        return vmodel;
    }

    public static boolean isConvertible(VertexFactor vf, int leftVar) throws IOException, InterruptedException {
        VertexFactor vf2 = null;
        boolean convertible = true;
        try {
            vf2 = new HalfspaceToVertex().apply(Convert.margVertexToHspace(vf), leftVar);
        }catch (Exception e){
            convertible = false;
        }

        return convertible && vf2 != null;
    }




}
