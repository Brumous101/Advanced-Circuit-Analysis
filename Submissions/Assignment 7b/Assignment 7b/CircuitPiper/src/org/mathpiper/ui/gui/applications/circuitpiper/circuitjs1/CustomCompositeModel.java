package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

import org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.elements.CustomCompositeElm;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class CustomCompositeModel implements Comparable<CustomCompositeModel> {

    public static HashMap<String, CustomCompositeModel> modelMap;

    public int flags, sizeX, sizeY;
    public String name;
    public String nodeList;
    public Vector<ExtListEntry> extList;
    public String elmDump;
    public boolean dumped;

    void setName(String n) {
        modelMap.remove(name);
        name = n;
        modelMap.put(name, this);
    }

    public static CustomCompositeModel getModelWithName(String name) {
        if (modelMap == null) {
            modelMap = new HashMap<String, CustomCompositeModel>();

            // create default stub model
            Vector<ExtListEntry> extList = new Vector<ExtListEntry>();
            extList.add(new ExtListEntry("gnd", 1));
            CustomCompositeModel d = createModel("default", "0", "GroundElm 1", extList);
            d.sizeX = d.sizeY = 1;
            modelMap.put(d.name, d);
        }
        CustomCompositeModel lm = modelMap.get(name);
        return lm;
    }

    public static CustomCompositeModel createModel(String name, String elmDump, String nodeList, Vector<ExtListEntry> extList) {
        CustomCompositeModel lm = new CustomCompositeModel();
        lm.name = name;
        lm.elmDump = elmDump;
        lm.nodeList = nodeList;
        lm.extList = extList;
        modelMap.put(name, lm);
        return lm;
    }

    public static void clearDumpedFlags() {
        if (modelMap == null) {
            return;
        }
        Iterator it = modelMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CustomCompositeModel> pair = (Map.Entry) it.next();
            pair.getValue().dumped = false;
        }
    }

    public static Vector<CustomCompositeModel> getModelList() {
        Vector<CustomCompositeModel> vector = new Vector<CustomCompositeModel>();
        Iterator it = modelMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CustomCompositeModel> pair = (Map.Entry) it.next();
            CustomCompositeModel dm = pair.getValue();
            vector.add(dm);
        }
        Collections.sort(vector);
        return vector;
    }

    public int compareTo(CustomCompositeModel dm) {
        return name.compareTo(dm.name);
    }

    CustomCompositeModel() {
    }

    public static void undumpModel(StringTokenizer st) {
        String name = CustomLogicModel.unescape(st.nextToken());
        CustomCompositeElm.lastModelName = name;
        CustomCompositeModel model = getModelWithName(name);
        if (model == null) {
            model = new CustomCompositeModel();
            model.name = name;
            modelMap.put(name, model);
        }
        model.undump(st);
    }

    void undump(StringTokenizer st) {
        flags = Integer.parseInt(st.nextToken());
        sizeX = Integer.parseInt(st.nextToken());
        sizeY = Integer.parseInt(st.nextToken());
        int extCount = Integer.parseInt(st.nextToken());
        int i;
        extList = new Vector<ExtListEntry>();
        for (i = 0; i != extCount; i++) {
            String s = CustomLogicModel.unescape(st.nextToken());
            int n = Integer.parseInt(st.nextToken());
            int p = Integer.parseInt(st.nextToken());
            int sd = Integer.parseInt(st.nextToken());
            extList.add(new ExtListEntry(s, n, p, sd));
        }
        nodeList = CustomLogicModel.unescape(st.nextToken());
        elmDump = CustomLogicModel.unescape(st.nextToken());
    }

    String arrayToList(String arr[]) {
        if (arr == null) {
            return "";
        }
        if (arr.length == 0) {
            return "";
        }
        String x = arr[0];
        int i;
        for (i = 1; i < arr.length; i++) {
            x += "," + arr[i];
        }
        return x;
    }

    String[] listToArray(String arr) {
        return arr.split(",");
    }

    public String dump() {
        dumped = true;
        String str = ". " + CustomLogicModel.escape(name) + " 0 " + sizeX + " " + sizeY + " " + extList.size() + " ";
        int i;
        for (i = 0; i != extList.size(); i++) {
            ExtListEntry ent = extList.get(i);
            if (i > 0) {
                str += " ";
            }
            str += CustomLogicModel.escape(ent.name) + " " + ent.node + " " + ent.pos + " " + ent.side;
        }
        str += " " + CustomLogicModel.escape(nodeList) + " " + CustomLogicModel.escape(elmDump);
        return str;
    }
}
