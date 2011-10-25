package ruc.irm.similarity.word.hownet2.sememe;

/**
 * 描述知网义原的基本对象, 出于性能考虑，把未用到的英文名称、定义等在加载时忽略, 更准确的做法是以[英文定义|中文定义]
 * 作为一个整理进行处理，不过绝大多数只根据中文定义就可以标识出来，因此忽略不计。<br/>
 * 义原编号采用父节点Id-子节点Id编码方式，如:
 * &lt;sememe cn="成功" define="{experiencer,scope}" en="succeed" id="1-1-2-1-4-5"/>
 * 义原的id表明了义原之间的上下位关系和义原的深度。
 * 
 * @author <a href="mailto:iamxiatian@gmail.com">夏天</a>
 * @organization 中国人民大学信息资源管理学院 知识工程实验室
 */
public class Sememe {
	/** 
	 * 义原编号,采用父节点Id-子节点Id编码方式，如&lt;sememe cn="成功" define="{experiencer,scope}" en="succeed" id="1-1-2-1-4-5"/>
	 * id表明了义原之间的上下位关系  
	 */
	private String id;
	/** 义原的中文名称*/
	private String cnWord;
	/** 义原的英文名称 */
	private String enWord;
	/** 义原的定义，如果没有(例如数量)，则为空串 */
	private String define;
	
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
	public Sememe(String id, String en, String cn, String define) {
		this.id = id;
		this.cnWord = cn;
		//为提高效率，减少内存空间利用，可去掉以下两行
		this.enWord = en;
		this.define = define;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		char ch = id.charAt(0);
		switch (ch) {
		case '1':
			return SememeType.Event;
		case '2':
			return SememeType.Entity;
		case '3':
			return SememeType.Attribute;
		case '4':
			return SememeType.Quantity;
		case '5':
			return SememeType.AValue;
		case '6':
			return SememeType.QValue;
		case '7':
			return SememeType.SecondaryFeature;
		case '8':
			return SememeType.Syntax;
		case '9':
			return SememeType.EventRoleAndFeature;
		default:
			return 0;
		}
	}
		
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("id=");
		sb.append(id);
		sb.append("; cnWord=");
		sb.append(cnWord);
		sb.append("; enWord=");
		sb.append(enWord);
		sb.append("; define=");
		sb.append(define);
		return sb.toString();
	}

}

