package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
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
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		// Stack to keep all tags
		Stack <TagNode> tags = new Stack <TagNode>();
		// First line in HTML file will be root(usually is "<HTML>") 
		String rootNoBrackets = sc.nextLine();
		rootNoBrackets = rootNoBrackets.substring(1, rootNoBrackets.length() - 1);
		root = new TagNode(rootNoBrackets, null, null);
		// Put the root or "<HTML>" into the stack
		tags.push(root);
		// While loop that will run until no lines left in HTML file
		while(sc.hasNextLine() == true) {
			// make the current HTML line a string 
			String currentLine = sc.nextLine();
			// boolean to help know if it is a tag which 
			// will be used later to add to tags stacks
			boolean tag = false;
			// see if it is a tag
			if (currentLine.charAt(0) == '<') {
				currentLine = currentLine.substring(1, currentLine.length() - 1);
				tag = true;
			}
			// see if it is the end of current tag
			if (currentLine.contains("/")) {
				tags.pop();
				// otherwise has to be some sort of text, table, etc. which we have to store
			} else {
				// create a new Node that will act as a placer
				TagNode newNode = new TagNode(currentLine, null, null);
				// if the first child is not null then we will have to make the new Node a sibling  
				if (tags.peek().firstChild != null) {
					// temporarily store the child in a separate Node
					TagNode child = tags.peek().firstChild;
					// this while loop is essential to create multiple siblings to a child and so that
					// no sibling gets overridden or replaced and creates a new location for the Node 
					while (child.sibling != null) {
					child = child.sibling;
					}
					// once broken out of the while statement which keeps on iterating until it finds 
					// an open spot for the new Node as a sibling, store as a sibling
					child.sibling = newNode; 
				} else {
					// if the child was null which will happen when we just have a 
					// root or when we move onto a new tag, we will simply store the new Node as the child
					tags.peek().firstChild = newNode;
				}
				// if the current Node is a tag we simply push it into the tag stack so we work within that tag
				if (tag == true) {
					tags.push(newNode);
			}
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		// We need to run inorder traversal and compare each Node within the tree and see if 
		// the current Node's tag is equal to the newTag for replacement.
		
		// We will make a separate method that will take in a Node because the 
		// unchangedinal replaceTag method doesn't do that for us
		if (root == null) {
			return;
		} else {
		replace(root, oldTag, newTag);
		}
	}
	
	private void replace(TagNode currentNode, String oldTag, String newTag) {
		// base case for recursion which indicates the end of traversal of firstChilds or siblings
		if (currentNode == null) {
			return ;
		}
		// We are dealing with Strings so we must use .equals to represent "equalness"
		if (currentNode.tag.equals(oldTag)) {
			// if the currentNode is equal to the tag that gets replaced we simply
			// make it the new tag by equating the Node to it
			currentNode.tag = newTag;
		}
		// Now we run recursion on all the children and siblings until we have went through each 
		// Node in the tree. This will happen for all children and siblings until we hit the base case
		//INORDER TRAVERSAL
		replace(currentNode.firstChild, oldTag, newTag);
		replace(currentNode.sibling, oldTag, newTag);
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		bold(root, row);
	}
	private void bold(TagNode currentNode, int row) {
		if (currentNode == null) {
			return;
		}
		if (currentNode.tag.equals("table")) {
			currentNode = currentNode.firstChild;
			for(int k = 1; k < row; k++) {
				currentNode = currentNode.sibling;
			}
			for (TagNode columnNode = currentNode.firstChild; columnNode != null; columnNode = columnNode.sibling) {
				columnNode.firstChild = new TagNode("b", columnNode.firstChild, null);
			}
		
		}
		bold(currentNode.firstChild, row);
		bold(currentNode.sibling, row);
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if (root != null) {
			while (traverseForRemove(root, tag)) {
				remove(root, root.firstChild, tag);
			}
		}
	}
	private boolean traverseForRemove(TagNode currentNode, String removeTag) {
		if (currentNode == null)
			return false;
		if (currentNode.tag.equals(removeTag) == true) {
			return true;
		}
		return traverseForRemove(currentNode.firstChild, removeTag) || traverseForRemove(currentNode.sibling, removeTag);
	}
	private void removeLI(TagNode currentNode) {
		if (currentNode == null) {
			return;
		}
		if (currentNode.tag.equals("li")) {
			String LI = "p";
			currentNode.tag = LI;
		}
		removeLI(currentNode.sibling);
	}
	private void remove(TagNode prevNode, TagNode currentNode, String removeTag) {
		if (prevNode == null) {
			return;
		}
		if (currentNode == null) {
			 return;
		} 
		if (currentNode.tag.equals(removeTag)) {
			if (olulCheck(removeTag) == true) {
				//TagNode tempNode = currentNode.firstChild;
				//while (tempNode != null)
				//{
					//if (tempNode.tag.equals("li"))
						//tempNode.tag = "p";
					//tempNode = tempNode.sibling;
				
				removeLI(currentNode.firstChild);
			}
				//if (currentNode.firstChild == null) {
					//return;
				//}
				//while (currentNode.firstChild.sibling != null) {
				//if (currentNode.firstChild.tag.equals("li")) {
					//currentNode.firstChild.tag = "p";
				//currentNode = currentNode.firstChild.sibling;
				//}	
			//}
			if (currentNode == prevNode.firstChild) {
				prevNode.firstChild = currentNode.firstChild;
				TagNode temp = currentNode.firstChild;
				while (temp.sibling != null) {
					temp = temp.sibling; 
				}
				temp.sibling = currentNode.sibling;
			}
			if (currentNode == prevNode.sibling) {
				TagNode temp = currentNode.firstChild;
				while (temp.sibling != null) {
					temp = temp.sibling; 
				}
				temp.sibling = currentNode.sibling;
				prevNode.sibling = currentNode.firstChild;
			}
			return;
		}
		prevNode = currentNode;
		remove(prevNode, currentNode.firstChild, removeTag);
		remove(prevNode, currentNode.sibling, removeTag);
	}
	private boolean olulCheck(String removeTag) {
		if (removeTag.equals("ol") || removeTag.equals("ul")) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	//public void addTag(String word, String tag) {
		//if(checkAddable(tag)== true) {
			//word = word.toLowerCase();
			//add(root, word, tag);
		//}
	//}
	private boolean checkTagEMB(String tag) {
		if (tag.equals("em") || tag.equals("b")) {
			return true;
		} else { 
			return false;
		}
	}
	public void addTag(String word, String tag) {
		if(checkTagEMB(tag) == true) { 
			addAttempt3(root, word.toLowerCase(), tag);
		}
	}

	
/*
	private void addAttempt2(TagNode currentNode, String word, String tag) {
		if(currentNode == null){
			return; 
		}
		addAttempt2(currentNode.firstChild, word, tag);
		addAttempt2(currentNode.sibling, word, tag);
		if(currentNode.firstChild == null) {
			while(currentNode.tag.toLowerCase().contains(word)) {
				StringTokenizer str = new StringTokenizer(currentNode.tag);
				String[] splitCurrentNode = currentNode.tag.split(" ");
				boolean sameWord = false;
				String prev = currentNode.tag.substring(0, currentNode.tag.toLowerCase().indexOf(word.toLowerCase()));
				String next = currentNode.tag.substring(currentNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
				int count = 0;
				String wordToTag = "";
				StringBuilder taggerString = new StringBuilder(currentNode.tag.length());
				for(int i = 0; i < splitCurrentNode.length; i++) {
					if(splitCurrentNode[i].toLowerCase().matches(word + "[.,?!:;]?")) {
						sameWord = true;
						wordToTag = splitCurrentNode[i];
						for(int j = i + 1; j < splitCurrentNode.length; j++){
							taggerString.append(splitCurrentNode[j]+" ");
						}
						break;
					}
				}
				if(!sameWord){
					return;
				}
				String result = taggerString.toString().trim();
				if(count == 0) {
					//String prev = currentNode.tag.substring(0, currentNode.tag.toLowerCase().indexOf(word.toLowerCase()));
					//String next = currentNode.tag.substring(currentNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
					currentNode.firstChild = new TagNode(wordToTag, null, null);
					currentNode.tag = tag;
					if(!result.equals("")) { 
						currentNode.sibling = new TagNode(prev + result, null, currentNode.sibling);
						currentNode = currentNode.sibling;
					}
				} else {
					TagNode wordToTagNode = new TagNode(wordToTag, null, null);
				//	String prev = currentNode.tag.substring(0, currentNode.tag.toLowerCase().indexOf(word.toLowerCase()));
					//String next = currentNode.tag.substring(currentNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
					TagNode newTag = new TagNode(prev + tag + next, wordToTagNode, currentNode.sibling);
					currentNode.sibling = newTag;
					currentNode.tag = currentNode.tag.replaceFirst(" " + wordToTag, "");
					if(!result.equals("")) {
						currentNode.tag = currentNode.tag.replace( result, "");
						newTag.sibling = new TagNode(result, null, newTag.sibling);
						currentNode = newTag.sibling;
					}
				}
			} 
		}
	}
	*/
	private boolean containsWord(TagNode currentNode, String word) {
		String newNode = currentNode.tag.toLowerCase();
		String newWord = word.toLowerCase();
		if (newNode.contains(newWord)) {
			return true;
		} else {
			return false;
		}
	}
	private boolean onlyContainsWord(TagNode currentNode, String word) {
		String newNode = currentNode.tag.toLowerCase();
		String newWord = word.toLowerCase();
		if (newNode.equals(newWord)) {
			return true;
		} else {
			return false;
		}
	}
	private void addAttempt3(TagNode currentNode,String word, String tag) {
		if (currentNode == null) {
			return ;
		} else if (containsWord(currentNode, word) == true) {
			// Initialize four Strings one for the complete tag, one for the word to be tagged, one for 
			// the ending punctuation that won't be in the word, and one to keep track of the original string
			String unchanged = currentNode.tag.substring(currentNode.tag.toLowerCase().indexOf(word.toLowerCase()), currentNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
			String beforeWordTagged = currentNode.tag.substring(0, currentNode.tag.toLowerCase().indexOf(word.toLowerCase()));
			String afterWordTagged = currentNode.tag.substring(currentNode.tag.toLowerCase().indexOf(word.toLowerCase()) + word.length());
			String punc = "";
			// This checks if the current nodes tag has the word we wanna tag in it
			if (onlyContainsWord(currentNode, word) == true) {
				// this is a rare condition where the word we wanna tag is the only thing in the node which means we simply tag it without using the 
				// other functionality of the before, after, punctuation strings
				currentNode.tag = tag;
				currentNode.firstChild = new TagNode (word, currentNode.firstChild, null);
		}	if (containsWord(currentNode, word) == true) {
			// back to original condition where we know our word is in the tag, but there are others words that we wanna keep untagged
			TagNode tempSiblingNode = currentNode.sibling;
				// This deals with the punctuation attached to our desired "word to be tagged"
				if (afterWordTagged.length() > 0) {
					if (afterWordTagged.length() > 1 && (endPuncCheckChar(afterWordTagged.charAt(0)) && endPuncCheckChar(afterWordTagged.charAt(1)))) {
						punc = "" + afterWordTagged.charAt(0);
						afterWordTagged = afterWordTagged.substring(1);
					}
				}	
				//This adds it to origanl so we can add new tag
				if ((afterWordTagged.length() == 0 || (afterWordTagged.length() >= 1 && (afterWordTagged.charAt(0) == ' ' || endPuncCheckChar(afterWordTagged.charAt(0)))))) {
					if (endPuncCheckString(afterWordTagged) == true) {
						unchanged = unchanged + afterWordTagged;
						afterWordTagged = "";
					}
					currentNode.tag = beforeWordTagged;
					currentNode.sibling = new TagNode(tag, new TagNode(unchanged + punc, null, null), null);
					if (afterWordTagged.length() > 0) {
						if (tempSiblingNode != null)
							currentNode.sibling.sibling = new TagNode(afterWordTagged, null, tempSiblingNode);
						else
							currentNode.sibling.sibling = new TagNode(afterWordTagged, null, null);
					} else if (tempSiblingNode != null) {
						currentNode.sibling.sibling = tempSiblingNode;
					} 
				} 
			}
		// SIBLING TRAVERSAL
			addAttempt3(currentNode.sibling.sibling, word, tag);
		} else {
			// INORDER TRAVERSALS AS SUAL
			addAttempt3(currentNode.firstChild, word, tag);
			addAttempt3(currentNode.sibling, word, tag);
		}
	}
	private boolean endPuncCheckString(String x) {
		if (x.equals("?") || x.equals(",") || x.equals("!") || x.equals(".")) {
			return true;
		} else { 
			return false;
		}
	}
	private boolean endPuncCheckChar(char x) {
		if (x == '?' || x == ',' || x == '!' || x == '.') {
			return true;
		} else { 
			return false;
		}
	}
	private boolean hasEndingPunc(char x) {
		if (x == '?' || x == '.' || x == ',' || x == '!') {
			return true;
		} else {
			return false;
		}
	}
	private boolean checkAddable(String tag) {
		if (tag.equals("b") || tag.equals("em")) {
			return true;
		} else {
			return false;
		}
	}
	/*
	private void addAttempt1(TagNode currentNode, String word, String tag) {
		if(currentNode == null){
			return; 
		}
		addAttempt1(currentNode.firstChild, word, tag);
		addAttempt1(currentNode.sibling, word, tag);
		if(currentNode.firstChild == null) {
			while(currentNode.tag.toLowerCase().contains(word)) {
				String[] splitCurrentNode = currentNode.tag.split(" ");
				StringTokenizer str = new StringTokenizer(currentNode.tag);
				int count = 0;
				int tokens = str.countTokens();
				StringBuilder taggerString = new StringBuilder(currentNode.tag.length());
				String tagWord = "";
				boolean sameWord = false;
				for(int i = 0; i < splitCurrentNode.length; i++) {
					if(splitCurrentNode[i].toLowerCase().matches(word+"[;:?!,.]")) {
						sameWord = true;
						tagWord = splitCurrentNode[i];
						for(int j = i + 1; j < splitCurrentNode.length; j++){
							taggerString.append(splitCurrentNode[j]+" ");
						}
						break;
					}
				}
				//while (str.hasMoreTokens()) {
					//String currentWord = str.nextToken();
					//if (str.nextToken().toLowerCase().matches(word + "[;:?!,.]")) {
						//sameWord = true;
						//wordToTag = str.nextToken();	
					//}
				//while (str.hasMoreTokens()) {
			//taggerString.append(str.nextToken());
		//}
			//}
				if(!sameWord){
					return;
				}
				String result = taggerString.toString().trim();
				if(count == 0) {
					currentNode.firstChild = new TagNode(tagWord, null, null);
					currentNode.tag = tag;
					if(!result.equals("")) { 
						currentNode.sibling = new TagNode(result, null, currentNode.sibling);
						currentNode = currentNode.sibling;
					
					}
				} else {
					currentNode.firstChild = new TagNode(tagWord, null, null);
					currentNode.tag = tag;
					if(!result.equals("")) { 
						currentNode.sibling = new TagNode(result, null, currentNode.sibling);
						currentNode = currentNode.sibling;
						TagNode nodeTagged = new TagNode(tagWord, null, null);
						TagNode newTag = new TagNode(tag, nodeTagged, currentNode.sibling);
						currentNode.sibling = newTag;
						currentNode.tag = currentNode.tag.replaceFirst(" " + tagWord, "");
						if(!result.equals("")) {
							currentNode.tag = currentNode.tag.replace(result, "");
							newTag.sibling = new TagNode(result, null, newTag.sibling);
							currentNode = newTag.sibling;					
					}

				}

			} 

		}
		}

	}	
			
*/
	
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
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
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
