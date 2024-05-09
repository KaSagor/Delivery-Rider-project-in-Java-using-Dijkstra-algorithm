
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class DeliveryRider extends JFrame {

    private JTextField startNodeField;
    private JTextField endNodeField;
    private final JButton findPathButton;
    private JTextArea resultArea;

    private static final int INF = Integer.MAX_VALUE;
    private static final int NUM_NODES = 5;
    private static final String[] nodeNames = {"A", "B", "C", "D", "E"};
    private static final int[][] graph = {
        {INF, 2, 1, INF, 3},
        {INF, 2, 1, 2, INF},
        {1, 1, INF, INF, 1},
        {INF, 2, 1, INF, INF},
        {3, INF, 1, INF, INF}
    };

    public DeliveryRider() {
        setTitle("Delivery Rider");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JLabel startNodeLabel = new JLabel("Pickup Point:");
        startNodeField = new JTextField(10);
        startNodeField.setText("A"); // Set 'A' as the default pickup point

        JLabel endNodeLabel = new JLabel("Delivery Point:");
        endNodeField = new JTextField(10);

        findPathButton = new JButton("Search Route");

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        findPathButton.addActionListener((var e) -> {
            String startNode = startNodeField.getText();
            String endNode = endNodeField.getText();

            int start = getNodeIndex(startNode);
            int end = getNodeIndex(endNode);

            if (start == -1 || end == -1) {
                resultArea.setText("Invalid Location. Please enter valid Location.");
                return;
            }

            int[] distances = dijkstra(graph, start);
            int shortestDistance = distances[end];

            if (shortestDistance == INF) {
                resultArea.setText("Sorry! no route found from " + startNode + " to " + endNode);
            } else {
                String shortestPath = reconstructPath(start, end, distances);
                int deliveryCharge = shortestDistance * 20;
                if (deliveryCharge < 50) {
                    deliveryCharge = 50;
                }
                resultArea.setText("Shortest path from " + startNode + " to " + endNode + " is:  " + shortestPath
                        + "\nDistance: " + shortestDistance + " KM"
                        + "\nDelivery Charge: " + deliveryCharge + " TK");
            }
        });

        add(startNodeLabel);
        add(startNodeField);
        add(endNodeLabel);
        add(endNodeField);
        add(findPathButton);
        add(scrollPane);

        pack();
        setLocationRelativeTo(null);
    }

    private int[] dijkstra(int[][] graph, int source) {
        int[] distances = new int[NUM_NODES];
        boolean[] visited = new boolean[NUM_NODES];

        for (int i = 0; i < NUM_NODES; i++) {
            distances[i] = INF;
            visited[i] = false;
        }

        distances[source] = 0;

        for (int count = 0; count < NUM_NODES - 1; count++) {
            int u = minDistance(distances, visited);
            visited[u] = true;

            for (int v = 0; v < NUM_NODES; v++) {
                if (!visited[v] && graph[u][v] != INF && distances[u] != INF
                        && distances[u] + graph[u][v] < distances[v]) {
                    distances[v] = distances[u] + graph[u][v];
                }
            }
        }

        return distances;
    }

    private int minDistance(int[] distances, boolean[] visited) {
        int min = INF;
        int minIndex = -1;

        for (int v = 0; v < NUM_NODES; v++) {
            if (!visited[v] && distances[v] < min) {
                min = distances[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    private String reconstructPath(int start, int end, int[] distances) {
        Stack<Integer> stack = new Stack<>();
        int current = end;

        while (current != start) {
            stack.push(current);
            current = findPreviousNode(current, distances);
        }

        stack.push(start);

        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.append(nodeNames[stack.pop()]);
            if (!stack.isEmpty()) {
                sb.append(" ---> ");
            }
        }

        return sb.toString();
    }

    private int findPreviousNode(int node, int[] distances) {
        for (int i = 0; i < NUM_NODES; i++) {
            if (graph[i][node] != INF && distances[node] == distances[i] + graph[i][node]) {
                return i;
            }
        }
        return -1;
    }

    private int getNodeIndex(String nodeName) {
        for (int i = 0; i < NUM_NODES; i++) {
            if (nodeNames[i].equals(nodeName)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DeliveryRider().setVisible(true);
        });
    }
}
