package pers.gwyog.gtneioreplugin.util;

import cpw.mods.fml.common.FMLLog;
import gregtech.api.GregTech_API;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import pers.gwyog.gtneioreplugin.GTNEIOrePlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class GT5CFGHelper {

    private static File F = GregTech_API.sWorldgenFile.mConfig.getConfigFile();

    public static String GT5CFGSmallOres(String veinName) {
        List<String> raw = new ArrayList<String>();
        List<String> rawbools = new ArrayList<String>();
        String st = null;
        Configuration c = new Configuration(F);
        ConfigCategory configCategory = c.getCategory("worldgen." + veinName);
        for (Property p : configCategory.getOrderedValues()) {
            if (p.isBooleanValue() && p.getBoolean()) {
                raw.add(p.getName() + "=" + p.getBoolean());
            }
        }
        test1(veinName, raw, rawbools);

        StringBuilder ret = new StringBuilder(" ");

        HashSet<String> rawBoolSet = new HashSet<>();
        if (!rawbools.isEmpty()) {
            rawbools = test2(rawbools, rawBoolSet);
            for (int i = 0; i < rawbools.size(); i++) {
                st = rawbools.get(i);
                for (int j = 0; j < DimensionHelper.DimName.length; j++) {
                    if (st.contains(DimensionHelper.DimName[j]))
                        if (st.contains("=true"))
                            ret.append(DimensionHelper.DimNameDisplayed[j]).append(",");
                }
            }
        }
        ret = new StringBuilder(ret.toString().trim());
        if ((ret.length() == 0) || ret.toString().equals(" "))
            ret = new StringBuilder("Not available in any Galactic Dim!");
        return ret.toString();
    }

    public static String GT5CFG(String veinName) {
//        FMLLog.info(veinName);
        if (F == null) {
            FMLLog.bigWarning("GT_CFG_NOT_found[0]");
            return "Error while Loading CFG";
        } else {
            try {
                int buffer = (int) (0.1 * Runtime.getRuntime().freeMemory());
                if (buffer > F.length()) {
                    buffer = (int) F.length();
                }
                //allocate 10% of free memory for read-in-buffer, if there is less than filesize memory available
                //FMLLog.info("GT_CFG_found[0]");
                FileReader in = new FileReader(F);
                //FMLLog.info("FileReader created");
                BufferedReader reader = new BufferedReader(in, buffer);
                //FMLLog.info("BufferedReader" +Integer.toString(buffer)+"created");
                String       line     = null;
                List<String> raw      = new ArrayList<>();
                List<String> rawBools = new ArrayList<>();
                Boolean[]    found    = new Boolean[2];
                found[0] = false;
                found[1] = false;

                do {
                    //FMLLog.info("erste");
                    //read until reached eof or mix {
                    line = reader.readLine();
                    //FMLLog.info("line: "+line);
                    if (line != null && line.trim().equals("mix {")) {
                        while (!((line == null) || ((line != null) && found[0]))) {
                            //FMLLog.info("zweite");
                            line = reader.readLine();
                            //read until reached eof or veinName {
                            //FMLLog.info("MIXst: "+line);
                            if (line != null && line.trim().equals(veinName + " {")) {
                                //FMLLog.info("VEINNAMEst: "+line);
                                while (!((line == null) || ((line != null) && found[0]))) {
                                    line = reader.readLine();
                                    if ((!(line == null)) && line.trim().equals("}")) {
                                        found[0] = true;
                                    }
                                    //FMLLog.info("dritte");
                                    //add everything below veinName { undtil } to raw
                                    raw.add(line);
                                }
                            }
                        }
                    }

                    if (line != null && line.trim().equals("dimensions {")) {
                        while (!((line == null) || ((line != null) && found[1]))) {
                            //FMLLog.info("zweite");
                            line = reader.readLine();
                            if (line != null && (line.trim().equals("mix {"))) {
                                while (!((line == null) || ((line != null) && found[1]))) {
                                    //FMLLog.info("dritte");
                                    line = reader.readLine();
                                    //read until reached eof or veinName {
                                    //FMLLog.info("MIXst: "+line);
                                    if (line != null && line.trim().equals(veinName + " {")) {
                                        //FMLLog.info("VEINNAMEst: "+line);
                                        while (!((line == null) || ((line != null) && found[1]))) {
                                            line = reader.readLine();
                                            if ((!(line == null)) && line.trim().equals("}")) {
                                                found[1] = true;
                                            }
                                            //FMLLog.info("vierte");
                                            //add everything below veinName { undtil } to raw
                                            raw.add(line);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } while (line != null);
                reader.close();//not needed anymore

                test1(veinName, raw, rawBools);

                StringBuilder ret = new StringBuilder(" ");

                HashSet<String> rawBoolSet = new HashSet<>();
                if (!rawBools.isEmpty()) {
                    //remove duplicates
                    rawBools = test2(rawBools, rawBoolSet);
                    //filter for dims set to true
                    for (String rawBool : rawBools) {
                        line = rawBool;
                        //FMLLog.info("RawBools:"+line);
                        for (int j = 0; j < DimensionHelper.DimName.length; j++) {
                            if (line.contains(DimensionHelper.DimName[j])) {
                                if (line.contains("=true")) {
                                    ret.append(DimensionHelper.DimNameDisplayed[j]).append(",");
                                }
                            }
                        }
                    }
                }
                ret = new StringBuilder(ret.toString().trim());
                //FMLLog.info("ret:"+ret);
                if (ret.toString().isEmpty() || ret.toString().equals(" ")) {
                    ret = new StringBuilder("Not available in any Galactic Dim!");
                }
                return ret.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error while Loading CFG";
            }
        }
    }

    private static List<String> test2(List<String> rawBools, HashSet<String> rawBoolSet) {
        String string;
        for (String rawBool : rawBools) {
            string = rawBool.replace("B:", "")
                            .replace("_true", "")
                            .replace("_false", "")
                            .replaceAll(" ", "")
                            .replaceAll("\"", "");
            rawBoolSet.add(string);
        }
        rawBools = new ArrayList<>(rawBoolSet);
        return rawBools;
    }

    private static void test1(String veinName, List<String> raw, List<String> rawBools) {
//        if (raw.isEmpty()) {
//            GTNEIOrePlugin.LOG.info("Config entry not found for Vein: " + veinName);
//            return;
//        }
//
//        raw.forEach(string -> {
//            Arrays.stream(DimensionHelper.DimName)
//                  .forEach(dimName -> {
//                      if (string.contains(dimName)) {
//                          rawBools.add(string);
//                      }
//                  });
//        });
        if (!raw.isEmpty()) {
            for (String string : raw) {
                //filter needed booleans from raw
                ///FMLLog.info("raw contains"+raw.get(i));
                for (int j = 0; j < DimensionHelper.DimName.length; j++) {
                    if (string.contains(DimensionHelper.DimName[j])) {
                        rawBools.add(string);
                    }
                }
                //FMLLog.info("rawBools: "+rawBools.get(i));
            }
        } else {
            GTNEIOrePlugin.LOG.info("Config entry not found for Vein: " + veinName);
        }
    }
}
