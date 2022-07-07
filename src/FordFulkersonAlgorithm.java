import java.util.*;
import java.io.File;

public class FordFulkersonAlgorithm {

    public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph) {
        ArrayList<Integer> path = new ArrayList<Integer>();
        if (source == destination) {
            path.add(source);
            return path;
        }

        LinkedList<LinkedList<Integer>> adjacent = new LinkedList<LinkedList<Integer>>();
        for (int i = 0; i < graph.getNbNodes(); i++) {
            LinkedList<Integer> neighbours = new LinkedList<Integer>();

            for (Edge e : graph.getEdges()) {
                if (e.nodes[0] == i) {
                    neighbours.add(e.nodes[1]);
                }
            }
            adjacent.add(i, neighbours);

        }

        path = recursion(source, destination, graph, new ArrayList<Integer>(), adjacent);

        if (path.size() == 0) {
            return new ArrayList<Integer>();
        }
        if (path.get(path.size() - 1) != destination) {
            return new ArrayList<Integer>();
        }
        return path;
    }

    private static ArrayList<Integer> recursion(Integer cur, Integer destination, WGraph graph, ArrayList<Integer> tempPath, LinkedList<LinkedList<Integer>> adjacent) {

        if (cur == destination) {
            tempPath.add(cur);
            return tempPath;
        }

        if (adjacent.get(cur).contains(destination)) {
            if (!tempPath.contains(destination) && graph.getEdge(cur, destination).weight > 0) {
                if (!tempPath.contains(cur)) {
                    tempPath.add(cur);
                }
                tempPath.add(destination);
                return tempPath;
            }
        }

        for (Integer n : adjacent.get(cur)) {
            if (!tempPath.contains(n) && graph.getEdge(cur, n).weight > 0) {
                if (tempPath.contains(destination)) {
                    break;
                }
                if (!tempPath.contains(cur)) {
                    tempPath.add(cur);
                    if (recursion(n, destination, graph, tempPath, adjacent).contains(destination)) {

                        return tempPath;
                    }
                    else{
                        tempPath.remove(cur);
                    }
                }
                else {
                    if(recursion(n, destination, graph, tempPath, adjacent).contains(destination)){
                        return tempPath;
                    }
                    else{
                        tempPath.remove(cur);
                    }

                }
            }
        }
        return tempPath;
    }



    public static int getSmallestC(ArrayList<Integer> path, WGraph residualGraph, WGraph graph) {
        int b = Integer.MAX_VALUE;
        for (int i = 1; i < path.size(); i++) {
            Edge e = residualGraph.getEdge(path.get(i - 1), path.get(i));
            Edge two = graph.getEdge(path.get(i - 1), path.get(i));
            if (e != null && e.weight >= 0) {
                if (two != null) {
                    //b=Math.min(b , two.weight-e.weight);
                    b = Math.min(b, e.weight);
                } else {
                    Edge second = graph.getEdge(path.get(i), path.get(i - 1));
                    b = Math.min(b, e.weight);
                }
            }
        }

        return b;
    }
    //Check that weight is bigger than 0 for each edge
    //check the conditions that are given in lecture
    //Check what happens if initially there is no path
    //source == sink

    public static String fordfulkerson(WGraph graph) {
        String answer = "";
        int maxFlow = 0;

        Integer source = graph.getSource();
        Integer destination = graph.getDestination();
        WGraph residualGraph = new WGraph(graph);

        WGraph testResidual = new WGraph(graph);
        for (Edge edge : testResidual.getEdges()) {
            edge.weight = 0;
        }
        ArrayList<Integer> path = new ArrayList<Integer>();

        for (int ed = 0; ed < residualGraph.getEdges().size(); ed++) {
            Edge e = residualGraph.getEdges().get(ed);
            int fi = e.nodes[0];
            int se = e.nodes[1];
            Edge adding = new Edge(se, fi, 0);
            if (residualGraph.getEdge(se, fi) == null) {
                residualGraph.addEdge(adding);
                testResidual.addEdge(adding);
            }

        }

        path = pathDFS(source, destination, residualGraph);

        if (path.size() == 1) {
            answer += maxFlow + "\n" + graph.toString();
            return answer;
        }

        while (path.size() != 0) {
            int b = getSmallestC(path, residualGraph, graph);
            for (int i = 1; i < path.size(); i++) {
                Edge e = residualGraph.getEdge(path.get(i - 1), path.get(i));
                Edge back=residualGraph.getEdge(path.get(i), path.get(i-1));
                Edge tester = new Edge(0, 0, 0);
                tester = graph.getEdge(path.get(i - 1), path.get(i));

                if (tester != null) {
                    e.weight -= b;
                    back.weight+=b;
                } else {
                    e.weight += b;
                    back.weight-=b;
                }
                testResidual.getEdge(path.get(i - 1), path.get(i)).weight += b;


            }
            maxFlow += b;

            path = pathDFS(source, destination, residualGraph);
        }

        for (int j = 0; j < graph.getEdges().size(); j++) {
            Edge changing = graph.getEdges().get(j);
            int x = changing.nodes[0];
            int y = changing.nodes[1];
            Edge checker = testResidual.getEdge(x, y);
            if (checker != null) {
                changing.weight = checker.weight;
            }

        }


        answer += maxFlow + "\n" + graph.toString();
        return answer;
    }


    public static void main(String[] args) {
        long startTime = System.nanoTime();
        String file = args[0];
        File f = new File(file);
        WGraph g = new WGraph(file);
        System.out.println(fordfulkerson(g));
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000; //convert to miliseconds
        System.out.println("The function took " + duration + " ms.");
    }
}
