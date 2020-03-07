package ek.zhou.item.pojo;

import org.springframework.beans.BeanUtils;

import ek.zhou.pojo.TbItem;

public class Item extends TbItem{
public Item(){
	
}
public Item(TbItem item){
	//将原对象中的参数装到新对象中
	BeanUtils.copyProperties(item, this);
}
public String[] getImages() {
	String image2 = this.getImage();
	if (image2 != null && !"".equals(image2)) {
		String[] strings = image2.split(",");
		return strings;
	}
	return null;
}
}
