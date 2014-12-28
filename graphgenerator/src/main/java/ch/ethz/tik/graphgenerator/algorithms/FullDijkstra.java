package ch.ethz.tik.graphgenerator.algorithms;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import ch.ethz.tik.graphgenerator.elements.Node;
import ch.ethz.tik.graphgenerator.elements.Graph;
import ch.ethz.tik.graphgenerator.elements.Adjacency;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class FullDijkstra implements IRoutingAlgorithm<Graph, Node> {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = "Dijkstra";

    private Integer[] distance = null;
    private Integer[] previous = null;
    private int[] scanned;

    private void init(Graph graph, Node source, Node target, boolean pollution) {
        distance = new Integer[graph.getNrOfNodes()];
        previous = new Integer[graph.getNrOfNodes()];
        scanned = new int[graph.getNrOfNodes()];

        // Initialize values
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source.getId() - 1] = 0;

        // Priority Queue
        Comparator<Integer> distanceComparator = getDistanceComparator();
        PriorityQueue<Integer> unvisited = new PriorityQueue<>(11,
                distanceComparator);
        unvisited.add(source.getId());

        while (!unvisited.isEmpty()) {
            int u = unvisited.poll();
            scanned[u - 1] = 1;

            for (Adjacency v : graph.getAdjacenciesForNode(u)) {
                if (scanned[v.getTarget() - 1] != 1) {
                    int weight = pollution ? v.getPollution() : v.getDistance();
                    int alt = distance[u - 1] + weight;
                    if (alt < distance[v.getTarget() - 1]) {
                        unvisited.remove(v.getTarget());
                        distance[v.getTarget() - 1] = alt;
                        previous[v.getTarget() - 1] = u;
                        unvisited.add(v.getTarget());
                    }
                }
            }
        }
    }

    private Comparator<Integer> getDistanceComparator() {
        Comparator<Integer> distanceComparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                if (distance[lhs - 1] < distance[rhs - 1]) {
                    return -1;
                } else if (distance[lhs - 1] > distance[rhs - 1]) {
                    return 1;
                }
                return 0;
            }
        };
        return distanceComparator;
    }

    /**
     * Returns a Set containing all nodes that are unreachable from a certain node.
     * @param graph
     * @param source
     * @return
     */
    public Set<Integer> getUnreachableNodes(Graph graph, Node source) {
        Set<Integer> unreachableNodes = Sets.newHashSet();
        source = graph.getClosestNodeTo(source);
        init(graph, source, source, true);
        for (int i = 0; i < scanned.length; i++) {
            if(scanned[i] != 1) {
                unreachableNodes.add(graph.getNode(i+1).getId());
            }
        }
        return unreachableNodes;
    }

    public List<Node> getShortestPath(Graph graph, Node source, Node target,
                                      boolean pollution) {
        List<Node> path = Lists.newLinkedList();
        Node closestSource = graph.getClosestNodeTo(source);
        Node closestTarget = graph.getClosestNodeTo(target);
        Preconditions.checkNotNull(closestSource);
        Preconditions.checkNotNull(closestTarget);
        Preconditions.checkArgument(closestSource.getId() != closestTarget
                .getId());
        init(graph, closestSource, closestTarget, pollution);
        int u = closestTarget.getId();
        path.add(graph.getNode(u));
        // UNTESTED
        while (previous[u - 1] != source.getId()) {
            path.add(0, graph.getNode(previous[u - 1]));
            u = previous[u - 1];
        }

        return path;
    }

}