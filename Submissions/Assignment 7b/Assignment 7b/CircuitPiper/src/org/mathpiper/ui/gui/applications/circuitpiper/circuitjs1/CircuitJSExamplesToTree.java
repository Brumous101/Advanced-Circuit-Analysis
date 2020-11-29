package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


public class CircuitJSExamplesToTree
{

    public static final DefaultMutableTreeNode getTreeNode(File filefile) throws IOException
    {

        Map<Integer, DefaultMutableTreeNode> levelNodes = new HashMap<Integer, DefaultMutableTreeNode>();
        BufferedReader reader = new BufferedReader(new FileReader(filefile));
        String line;
       
        DefaultMutableTreeNode node = null;
        
        int level = 0;
        
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        node = rootNode;
        levelNodes.put(level, node);


        while ((line = reader.readLine()) != null)
        {
            //System.out.println(line);

            if (line.charAt(0) == '#')
            {
                continue;
            }
            else if (line.charAt(0) == '+')
            {
                level++;
                
                String nodeName = line.substring(1);
                
                node = new DefaultMutableTreeNode(nodeName);

                levelNodes.put(level, node);

                DefaultMutableTreeNode parent = levelNodes.get(level - 1);

                parent.add(node);

            }
            else if (line.charAt(0) == '-')
            {   
                level--;
                node  = levelNodes.get(level);
            }
            else
            {
                int i = line.indexOf(' ');
                if (i > 0)
                {
                    String title = line.substring(i + 1);
                    boolean first = false;
                    if (line.charAt(0) == '>')
                    {
                        first = true;
                    }
                    String fileName = line.substring(first ? 1 : 0, i);
                    
                    DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(fileName);
                    
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(title);
                    
                    newNode.add(fileNode);

                    node.add(newNode);
                                        
                    
                }
            }
            
            
            

        }
        reader.close();
        return rootNode;
    }

    
    public static void print(TreeNode treeNode, String indent)
    {
        int childCount = treeNode.getChildCount();
        
        if(treeNode.getChildCount() > 0)
        {
            System.out.println(indent + treeNode.toString());
        }
        
        for(int index = 0; index < childCount; index++)
        {
            print(treeNode.getChildAt(index), indent + "   ");
            
        }
    }
    

    
    public static void main(String[] args) throws Exception
    {
        File file = new File("/home/tkosan/git2/circuitjs1/src/com/lushprojects/circuitjs1/public/setuplist.txt");
        
        DefaultMutableTreeNode treeNode = getTreeNode(file);
        
        JTree jTree = new JTree(treeNode);
        
        JFrame frame = new JFrame();
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        
        panel.add(jTree);
        
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       
        frame.getContentPane().add(scrollPane);
        
        frame.setSize(400, 400);
        
        frame.pack();
        frame.setLocationByPlatform(true);
        
        //frame.setVisible(true);
        
        print(treeNode, "");
        
    }
}
