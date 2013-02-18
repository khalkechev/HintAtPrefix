package packageTrie;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;

public class TrieNode {
	//ключ и его ранк (чем больше ранк - тем популярнее запрос)
	private String key;
	private int rank;
	//список дочерних узлов
	private SortedMap<Character, TrieNode> edges;
	//top-10 запросов и их ранки - находящихся в поддереве с вершиной в данном узле
	TreeMap<Integer, String> top = new TreeMap<Integer, String>();

	//добавление дочернего узла
	TrieNode addEdge(char c) {
		if ( edges == null ) {
			edges = new TreeMap<Character, TrieNode>(); 
		}
		TrieNode childNode = new TrieNode();
		edges.put(c, childNode);
		return childNode;
	}

	//возвращает дочерний узел по ключу или null, если такого нет
	TrieNode traverse(char c) {
		return (edges == null) ? null : edges.get(c);
	}

	//удаление дочернего узла
	TrieNode deleteEdge(char c) {
		return (edges == null) ? null : edges.remove(c);
	}

	//итератор дочерних узлов
	Iterator<TrieNode> getChildren() {
		return (edges == null) ? null : edges.values().iterator();
	}
	//итератор top
	Iterator<String> getTop() {
		return (top == null) ? null : top.values().iterator();
	}

	//назначить значение ключа текущему узлу
	void setKey(String key) {
		this.key = key;
	}

	//узнать значение ключа текущего узла
	String getKey() {
		return key;
	}

	//назначить значение ранка текущему узлу
	void setRank(int rank) {
		this.rank = rank;
	}

	//узнать значение ключа текущего узла
	int getRank() {
		return rank;
	}

	//узнать значение минимального ранга среди запросов top
	int getMinRank() {
		if (top.isEmpty()) { return 0;}
		else { return top.firstKey();}
	}

	//узнать количество дочерних узлов
	public int getChildrenCnt() {
		return (edges == null) ? 0 : edges.size();
	}
}