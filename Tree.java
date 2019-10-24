package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode,
 * with fields for tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed in to the
	 * constructor and stored in the sc field of this object.
	 * 
	 * The root of the tree that is built is referenced by the root field of this
	 * object.
	 */

	public void build() {
		/** COMPLETE THIS METHOD **/

		if (!sc.hasNextLine()) {
			return;
		}

		String str = sc.nextLine().toString();
		String tmpstr;

		if (str.charAt(0) == '<') {
			tmpstr = str.substring(1, str.length() - 1);
		} else {
			tmpstr = str;
		}

		Stack<TagNode> stack = new Stack<TagNode>();
		root = new TagNode(tmpstr, null, null);
		stack.push(root);

		while (sc.hasNextLine()) {
			String next = sc.nextLine();
			int bracket = 0;
			if (next.charAt(0) == '<') {
				bracket = 1;
				next = next.substring(1, next.length() - 1);
			}

			if ((bracket == 1) && (next.charAt(0) == '/')) {
				// if(!stack.isEmpty())
				stack.pop();
			}

			else {
				TagNode temp = new TagNode(next, null, null);
				// if (!stack.isEmpty()) {
				if (stack.peek().firstChild == null)
					stack.peek().firstChild = temp;
				else {
					TagNode childNode = stack.peek().firstChild;
					while (childNode.sibling != null)
						childNode = childNode.sibling;
					childNode.sibling = temp;
					// }
				}
				if (bracket == 1) {
					stack.push(temp);
				}
			}
		}
	}

	/*
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * 
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		recReplace(oldTag, newTag, root);

	}

	private void recReplace(String oldTag, String newTag, TagNode root) {
		if (root == null) {
			return;
		}
		if (root.tag.equals(oldTag)) {
			root.tag = newTag;
		}
		recReplace(oldTag, newTag, root.sibling);
		recReplace(oldTag, newTag, root.firstChild);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The
	 * boldface (b) tag appears directly under the td tag of every column of this
	 * row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		bold(root, row);
	}

	private void bold(TagNode root, int row) {
		if (root == null) {
			return;
		}
		if (root.tag.equals("table")) {
			TagNode table = root.firstChild;
			for (int i = 0; i < row - 1; i++) {
				if (table.sibling != null) {
					table = table.sibling;
				}
			}
			// TagNode tCol = table.firstChild;
			// while(tCol != null){
			for (TagNode tCol = table.firstChild; tCol != null; tCol = tCol.sibling) {
				TagNode boldTag = new TagNode("b", null, null);
				boldTag.firstChild = tCol.firstChild;
				tCol.firstChild = boldTag;
			}
		}
		bold(root.firstChild, row);
		bold(root.sibling, row);

	}

	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b,
	 * all occurrences of the tag are removed. If the tag is ol or ul, then All
	 * occurrences of such a tag are removed from the tree, and, in addition, all
	 * the li tags immediately under the removed tag are converted to p tags.
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		root = this.recRemove(tag, root);

	}

	private TagNode recRemove(String tag, TagNode newRoot) { // tagnode
		if (newRoot == null) {
			return null;
		}
		String line = newRoot.tag;
		TagNode lastNode = new TagNode(null, null, null);

		newRoot.firstChild = this.recRemove(tag, newRoot.firstChild);
		if ((tag.equals("ol") || tag.equals("ul")) && line.equals(tag)) {
			lastNode = newRoot.firstChild;
			while (lastNode != null) {
				if (lastNode.tag.equals("li")) {
					lastNode.tag = "p";
				}
				lastNode = lastNode.sibling;
			}
			lastNode = newRoot.firstChild;
			while (lastNode.sibling != null) {
				lastNode = lastNode.sibling;
			}
			lastNode.sibling = newRoot.sibling;
			newRoot = newRoot.firstChild;
		} else if ((tag.equals("p") || tag.equals("em") || tag.equals("b")) && line.equals(tag)) {
			lastNode = newRoot.firstChild;
			while (lastNode.sibling != null) {
				lastNode = newRoot.sibling;
			}
			newRoot = newRoot.firstChild;
		}
		newRoot.sibling = this.recRemove(tag, newRoot.sibling);
		return newRoot;
	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag  Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		hasWord(word, tag, root);

	}

	private void hasWord(String word, String tag, TagNode root) {
		if (root == null) {
			return;
		}

		String orig = root.tag;
		String lower = orig.toLowerCase();
		String lower2 = word.toLowerCase();
		String punc = ".,?!:;";
		String sub = "";
		String subBefore = "";
		String subAfter = "";
		int startIndex = 0;
		int endIndex = 0;

		if (lower.contains(lower2)) { // changed word to lower2

			for (int i = 0; i < lower.length(); i++) {
				if (lower.charAt(i) == lower2.charAt(0)) {
					// String tmp = lower.substring(i, lower.length() - 1); // tried just i, tried
					// length-1
					// startIndex = i;
					for (int j = i; (j - i) <= word.length(); j++) { // changed lower to word
						// if((j-i) == word.indexOf(word.length()-1))
						if ((j - i) == word.length()) { // changed tmp to lower
							endIndex = j;
							startIndex = i;
							// System.out.println("start =" + startIndex);
							// System.out.println("end =" + endIndex);
							break;
						}
					}
					break;
				}
			}
			sub = lower.substring(startIndex, endIndex); // changed orig to lower
			// System.out.println("sub = " + sub);
			subBefore = orig.substring(0, startIndex);
			// System.out.println("before = " + subBefore);
			subAfter = orig.substring(endIndex, orig.length()); /// removed length-1
			// System.out.println("after = " + subAfter);

			if (lower.equals(sub)) { /// changed lower2 to sub
				TagNode oldWord = new TagNode(word, null, null);
				root.tag = tag;
				root.firstChild = oldWord;
				root = root.firstChild;
			} else if (lower.equals(sub + subAfter)) {
				if ((subAfter.length() == 1) && punc.contains(subAfter)) {
					TagNode oldWord = new TagNode(word + subAfter, null, null);
					root.tag = tag;
					root.firstChild = oldWord;
					root = root.firstChild;
				}
			} else if (subBefore.length() > 0 && subBefore.charAt(subBefore.length()-1) == ' ') {
				
				// if(subB)
				root.tag = subBefore;
				TagNode root2 = new TagNode(sub, null, null);
				// root.sibling = root2;
				if (subAfter.length() > 0) {
					if ((subAfter.length() == 1) && punc.contains(subAfter)) {
						root2.tag = sub + subAfter;
					} else {
						TagNode root3 = new TagNode(subAfter, null, root.sibling);
						root2.sibling = root3;
						root.sibling = root2;
					}
				} else {
					root.sibling = root2;
				}
			}

		}

		hasWord(word, tag, root.sibling);
		hasWord(word, tag, root.firstChild);
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes new
	 * lines, so that when it is printed, it will be identical to the input file
	 * from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines.
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");
			}
		}
	}

	/**
	 * Prints the DOM tree.
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("      ");
			}
			;
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level + 1);
			}
		}
	}
}
