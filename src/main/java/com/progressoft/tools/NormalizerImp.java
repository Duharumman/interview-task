package com.progressoft.tools;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NormalizerImp implements Normalizer {
    List<List < String> > attributesLin=new ArrayList<>();
    List <String>header = new ArrayList<>();
    Integer ans = null;


    public List<BigDecimal> readCvs(Path csvPath,Path destPath, String colToStandardize) {
        try {
            if(destPath == null)
                throw new IllegalArgumentException("source file not found");
            if(!Files.exists(csvPath.getParent()))
                Files.createDirectories(csvPath.getParent());
            if(!Files.exists(destPath))
                Files.createFile(destPath);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        List<BigDecimal> col = new ArrayList<>();
        BufferedReader br;
        try {
            br = Files.newBufferedReader(csvPath, StandardCharsets.US_ASCII);
            String line ;
            line = br.readLine();

            String[] cloName = line.split(",");
             header = new ArrayList<String>(Arrays.asList(cloName));

            for (int i = 0; i < cloName.length; i++) {
                if (cloName[i].equals(colToStandardize))
                    ans = i;
            }
            if(ans == null) {
                throw new IllegalArgumentException("column " + colToStandardize + " not found");
            }
            line = br.readLine();
            while (line != null) {
                String[] attributes = line.split(",");

                attributesLin.add(new ArrayList(Arrays.asList(attributes)));
                line = br.readLine();

                col.add(new BigDecimal(attributes[ans]));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return col;
    }

    public void writeCvs( Path destPath ,List<BigDecimal>values , String colNAme){
        String [] arr = new String[values.size()];
        for(int i=0; i< attributesLin.size();i++){
            attributesLin.get(i).add(ans+1,values.get(i).toString());
        }
        try (PrintWriter writer = new PrintWriter(new File(String.valueOf(destPath)))) {
            StringBuilder sb = new StringBuilder();
            header.add(++ans,colNAme);

            for (int i=0 ; i<header.size();i++){
                if(i== header.size()-1)
                    sb.append(header.get(i));
                else
                    sb.append(header.get(i)+",");
            }
            sb.append("\n");
            writer.write(sb.toString());
            sb=new StringBuilder();
            for (int i =0; i<attributesLin.size(); i++) {
                sb = new StringBuilder();
                for (int j = 0; j < attributesLin.get(i).size(); j++) {
                    if (j == attributesLin.get(i).size() - 1)
                        sb.append(attributesLin.get(i).get(j));
                    else
                        sb.append(attributesLin.get(i).get(j) + ",");
                }
                sb.append("\n");
                writer.write(sb.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize) {


        List<BigDecimal> colToNormlz = readCvs(csvPath, destPath ,colToStandardize);
        List<BigDecimal> colToNormlzAns = new ArrayList<>();

        ScoringSummaryImpl scoringSummary = new ScoringSummaryImpl(colToNormlz);

        BigDecimal meanAns=scoringSummary.mean();

        BigDecimal standardDeviationAns=scoringSummary.standardDeviation();
        for(int i=0 ;i <colToNormlz.size();i++){
            BigDecimal val=BigDecimal.ZERO;

            val=colToNormlz.get(i).subtract(meanAns);
            val=val.divide(standardDeviationAns,2,RoundingMode.HALF_EVEN);

            colToNormlzAns.add(val);
        }
        writeCvs(destPath ,colToNormlzAns , colToStandardize+"_z");
        return  scoringSummary;
    }

    @Override
    public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize) {


        List<BigDecimal> colToNormlz = readCvs(csvPath, destPath ,colToNormalize);
        List<BigDecimal> colToNormlzValue = new ArrayList<>();

        ScoringSummaryImpl scoringSummary = new ScoringSummaryImpl(colToNormlz);

        BigDecimal mn = scoringSummary.min();
        BigDecimal mx = scoringSummary.max();
        BigDecimal norm = mx.subtract(mn);

        norm = norm.equals(BigDecimal.ZERO) ? norm.add(BigDecimal.ONE) : norm;
        for (int i = 0; i < colToNormlz.size(); i++) {
            BigDecimal sub =BigDecimal.ZERO;
            BigDecimal value  =BigDecimal.ZERO;

            MathContext mc = new MathContext(2);

            sub = colToNormlz.get(i).subtract(mn);
            sub=BigDecimal.valueOf(sub.doubleValue()).setScale(2, RoundingMode. HALF_EVEN);

            value = sub.divide(norm,mc);
            value=BigDecimal.valueOf(value.doubleValue()).setScale(2, RoundingMode. HALF_EVEN);

            colToNormlzValue.add(value);
        }
        writeCvs(destPath ,colToNormlzValue  , colToNormalize+"_mm");
        return scoringSummary;
    }
}
