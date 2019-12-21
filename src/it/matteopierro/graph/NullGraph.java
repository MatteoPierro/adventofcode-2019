package it.matteopierro.graph;

import org.jgrapht.Graph;
import org.jgrapht.GraphType;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class NullGraph <T> implements Graph<T, DirectedGraphEdge<T>> {
    @Override
    public Set<DirectedGraphEdge<T>> getAllEdges(T t, T v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectedGraphEdge<T> getEdge(T t, T v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Supplier<T> getVertexSupplier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Supplier<DirectedGraphEdge<T>> getEdgeSupplier() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectedGraphEdge<T> addEdge(T t, T v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addEdge(T t, T v1, DirectedGraphEdge<T> tDirectedGraphEdge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T addVertex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addVertex(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsEdge(T t, T v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsEdge(DirectedGraphEdge<T> tDirectedGraphEdge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsVertex(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<DirectedGraphEdge<T>> edgeSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int degreeOf(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<DirectedGraphEdge<T>> edgesOf(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int inDegreeOf(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<DirectedGraphEdge<T>> incomingEdgesOf(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int outDegreeOf(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<DirectedGraphEdge<T>> outgoingEdgesOf(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAllEdges(Collection<? extends DirectedGraphEdge<T>> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<DirectedGraphEdge<T>> removeAllEdges(T t, T v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectedGraphEdge<T> removeEdge(T t, T v1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeEdge(DirectedGraphEdge<T> tDirectedGraphEdge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeVertex(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> vertexSet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getEdgeSource(DirectedGraphEdge<T> tDirectedGraphEdge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getEdgeTarget(DirectedGraphEdge<T> tDirectedGraphEdge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GraphType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getEdgeWeight(DirectedGraphEdge<T> tDirectedGraphEdge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEdgeWeight(DirectedGraphEdge<T> tDirectedGraphEdge, double v) {
        throw new UnsupportedOperationException();
    }
}
