package ek.zhou.service;

import ek.zhou.common.pojo.TaotaoResult;
import ek.zhou.pojo.TbItemDesc;
/**
 * 商品描述的service接口
 * @author Administrator
 *
 */
public interface ItemDescService {
/**
 * 根据id查询商品描述详情
 * @param tbItemDesc
 * @return
 */
public TaotaoResult getItemDescById(Long id);
}
