//package ek.zhou.service.imp;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.github.pagehelper.PageHelper;
//import ek.zhou.service.BaseService;
//import tk.mybatis.mapper.common.Mapper;
//import tk.mybatis.mapper.entity.Example;
///**
// * 基础service的实现类
// * @author Administrator
// *
// * @param <T>
// */
//public class BaseServiceImp<T> implements BaseService<T> {
//	@Autowired
//	private Mapper<T> mapper;
//	private Class<T> clazz;
//	
//	public BaseServiceImp() {
//		//获取父类type
//		Type type = this.getClass().getGenericSuperclass();
//		//强转为ParameterizedType ,可以使用获取泛型类型的方法
//		ParameterizedType parameterizedType = (ParameterizedType) type;
//		//获取泛型的class
//		this.clazz = (Class<T>)parameterizedType.getActualTypeArguments()[0];
//	}
//	@Override
//	public T queryById(Long id) {
//		T t = this.mapper.selectByPrimaryKey(id);
//		return t;
//	}
//
//	@Override
//	public List<T> queryAll() {
//		return this.mapper.select(null);
//	}
//
//	@Override
//	public Integer queryCountByWhere(T t) {
//		return this.mapper.selectCount(t);
//	}
//
//	@Override
//	public List<T> queryListByWhere(T t) {
//		return this.mapper.select(t);
//	}
//
//	@Override
//	public List<T> queryByPage(Integer page, Integer rows) {
//		PageHelper.startPage(page, rows);
//		return this.mapper.select(null);
//	}
//
//	@Override
//	public T queryOne(T t) {
//		return this.mapper.selectOne(t);
//	}
//
//	@Override
//	public void save(T t) {
//		this.mapper.insert(t);
//	}
//
//	@Override
//	public void saveSelective(T t) {
//		this.mapper.insertSelective(t);
//	}
//
//	@Override
//	public void updateById(T t) {
//		this.mapper.updateByPrimaryKey(t);
//	}
//
//	@Override
//	public void updateByIdSelective(T t) {
//		this.mapper.updateByPrimaryKeySelective(t);
//	}
//
//	@Override
//	public void deleteById(Long id) {
//		this.mapper.deleteByPrimaryKey(id);
//	}
//
//	@Override
//	public void deleteByIds(List<Object> ids) {
//		//声明条件
//		Example example = new Example(this.clazz);
//		example.createCriteria().andIn("id", ids);
//		this.mapper.deleteByExample(example);
//	}
//
//}
