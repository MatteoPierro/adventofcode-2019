package it.matteopierro.graph;

import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultGraphType;

import java.util.HashSet;
import java.util.Set;

public abstract class InfiniteUndirectedGraph<T> extends NullGraph<T> {
    public abstract Set<T> adjacentVertices(T vertex);

    @Override
    public abstract boolean containsVertex(T vertex);

    @Override
    public DirectedGraphEdge<T> getEdge(T sourceVertex, T targetVertex) {
        if (containsEdge(sourceVertex, targetVertex)) {
            return new DirectedGraphEdge<>(sourceVertex, targetVertex);
        }
        return null;
    }

    @Override
    public boolean containsEdge(DirectedGraphEdge<T> edge) {
        return containsEdge(edge.source, edge.target);
    }

    @Override
    public boolean containsEdge(T sourceVertex, T targetVertex) {
        if (sourceVertex == null) return false;
        if (targetVertex == null) return false;
        if (!containsVertex(sourceVertex)) return false;
        if (!containsVertex(targetVertex)) return false;
        return adjacentVertices(sourceVertex).contains(targetVertex);
    }

    @Override
    public int degreeOf(T vertex) {
        return 2 * adjacentVertices(vertex).size();
    }

    @Override
    public int inDegreeOf(T vertex) {
        return degreeOf(vertex) / 2;
    }

    @Override
    public Set<DirectedGraphEdge<T>> incomingEdgesOf(T vertex) {
        return edgesFor(vertex);
    }

    @Override
    public int outDegreeOf(T vertex) {
        return degreeOf(vertex) / 2;
    }

    @Override
    public Set<DirectedGraphEdge<T>> outgoingEdgesOf(T vertex) {
        return edgesFor(vertex);
    }

    @Override
    public T getEdgeSource(DirectedGraphEdge<T> edge) {
        return edge.source;
    }

    @Override
    public T getEdgeTarget(DirectedGraphEdge<T> edge) {
        return edge.target;
    }

    @Override
    public GraphType getType() {
        return new DefaultGraphType.Builder()
                .directed()
                .allowSelfLoops(false)
                .modifiable(false)
                .build();
    }

    @Override
    public double getEdgeWeight(DirectedGraphEdge<T> edge) {
        return DEFAULT_EDGE_WEIGHT;
    }

    private Set<DirectedGraphEdge<T>> edgesFor(T vertex) {
        var edges = new HashSet<DirectedGraphEdge<T>>();

        for (T adjacent : adjacentVertices(vertex)) {
            edges.add(new DirectedGraphEdge<>(vertex, adjacent));
        }

        return edges;
    }
}
