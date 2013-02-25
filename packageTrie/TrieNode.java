package packageTrie;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * TrieNode
 */
public class TrieNode {
	//ключ и его ранк (чем больше ранк - тем популярнее запрос)
	private String key;
	private int rank;
	//список дочерних узлов
	private SortedMap<Character, TrieNode> edges;
	//top-10 запросов и их ранки - находящихся в поддереве с вершиной в данном узле
	TreeMap<Integer, String> top = new TreeMap<Integer, String>();

	/**  
	 * Добавление дочернего узла
	 *
	 * @param c
	 *
	 */
	TrieNode addEdge(char c) {
		if ( edges == null ) {
			edges = new TreeMap<Character, TrieNode>(); 
		}
		TrieNode childNode = new TrieNode();
		edges.put(c, childNode);
		return childNode;
	}

	/**  
	 * Возвращает дочерний узел по ключу или null, если такого нет
	 *
	 * @param c
	 *         ключ
	 *
	 * @return узел
	 */
	TrieNode traverse(char c) {
		return (edges == null) ? null : edges.get(c);
	}

	/**  
	 * Удаление дочернего узла по ключу
	 *
	 * @param c
	 *
	 */
	TrieNode deleteEdge(char c) {
		return (edges == null) ? null : edges.remove(c);
	}

	//итератор дочерних узлов
	Iterator<TrieNode> getChildren() {
		return (edges == null) ? null : edges.values().iterator();
	}
	//итератор top запросов
	Iterator<String> getTop() {
		top.descendingMap();
		return (top == null) ? null : top.values().iterator();
	}

	/**  
	 * Назначение значения ключа узлу
	 *
	 * @param key
	 *           ключ
	 */
	void setKey(String key) {
		this.key = key;
	}

	/**  
	 * Возвращает значение ключа данного узла
	 *
	 * @return ключ
	 */
	String getKey() {
		return key;
	}

	/**  
	 * Назначение значения ранка узлу
	 *
	 * @param rank
	 *            ранк
	 */
	void setRank(int rank) {
		this.rank = rank;
	}

	/**  
	 * Возвращает значение ранка данного узла
	 *
	 * @return ранк
	 */
	int getRank() {
		return rank;
	}

	/**  
	 * Возвращает значение минимального ранка среди ранков
	 * десяти самых популярных запросов в поддереве с вершиной в данном узле
	 *
	 * @return ранк
	 */	
	int getMinRank() {
		if (top.isEmpty()) { return 0;}
		else { return top.firstKey();}
	}


	/**  
	 * Возвращает количество дочерних узлов
	 *
	 * @return количество дочерних узлов
	 */
	public int getChildrenCnt() {
		return (edges == null) ? 0 : edges.size();
	}
}
