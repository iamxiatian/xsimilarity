package ruc.irm.similarity.word.hownet.sememe;

/**
 * 描述知网义原的基本对象, 出于性能考虑，把未用到的英文名称、定义等在加载时忽略, 更准确的做法是以[英文定义|中文定义]
 * 作为一个整理进行处理，不过绝大多数只根据中文定义就可以标识出来，因此忽略不计。
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 * @deprecated
 */
public class Sememe {
	/** 义原编号 */
	private int id;
	/** 指向上位义元号 */
	private int parentId;
	/** 义原在义原树中的深度 */
	private int depth;
	/** 义原的中文名称*/
	private String cnWord;
	/** 义原的英文名称 */
	private String enWord;
	/** 义原的定义，如果没有(例如数量)，则为空串 */
	private String define;
	/** 义原的类型 */
	private int type;	

	/**
	 * 每一行的形式为：be|是 {relevant,isa}/{relevant,descriptive} 
	 * <br/>或者 official|官 [#organization|组织,#employee|员] 
	 * <br/>或者 amount|多少 
	 * <br/>把相应的部分赋予不同的属性
	 * 出于性能考虑，把未用到的英文名称、定义等忽略
	 * @param id
	 * @param parentId
	 * @param item 读取文件中的一行
	 */
	public Sememe(int id, int parentId, int depth, String item) {
		this.id = id;
		this.parentId = parentId;
		this.depth = depth;
		
		int pos = item.indexOf('|');
		if (pos < 0) {
			this.cnWord = item;
			this.enWord = item;
		} else {
			this.enWord = item.substring(0, pos);

			// 去掉"|"符号
			String nextPart = item.substring(pos + 1);
			pos = nextPart.indexOf(' ');
			if (pos <= 0) {
				this.cnWord = nextPart;
			} else {
				this.cnWord = nextPart.substring(0, pos);
				this.define = nextPart.substring(pos).trim();
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getCnWord() {
		return cnWord;
	}

	public void setCnWord(String cnWord) {
		this.cnWord = cnWord;
	}

	public String getEnWord() {
		return enWord;
	}

	public void setEnWord(String enWord) {
		this.enWord = enWord;
	}

	public String getDefine() {
		return define;
	}

	public void setDefine(String define) {
		this.define = define;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("id=");
		sb.append(id);
		sb.append("; parentId=");
		sb.append(parentId);
		sb.append("; depth=");
		sb.append(depth);
		sb.append("; cnWord=");
		sb.append(cnWord);
		sb.append("; enWord=");
		sb.append(enWord);
		sb.append("; define=");
		sb.append(define);
		return sb.toString();
	}

}

