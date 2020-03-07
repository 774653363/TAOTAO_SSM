<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div>
	 <ul id="contentCategory" class="easyui-tree">  </ul>
</div>
<div id="contentCategoryMenu" class="easyui-menu" style="width:120px;" data-options="onClick:menuHandler">
    <div data-options="iconCls:'icon-add',name:'add'">添加</div>
    <div data-options="iconCls:'icon-remove',name:'rename'">重命名</div>
    <div class="menu-sep"></div>
    <div data-options="iconCls:'icon-remove',name:'delete'">删除</div>
</div>
<script type="text/javascript">
$(function(){
	//生成内容分类树
	$("#contentCategory").tree({
		url : '/content/category/list',
		animate: true,
		method : "GET",
		onContextMenu: function(e,node){
            e.preventDefault();
            $(this).tree('select',node.target);
            $('#contentCategoryMenu').menu('show',{
                left: e.pageX,
                top: e.pageY
            });
        },
        //编辑后触发的函数
        onAfterEdit : function(node){
        	//获取树
        	var _tree = $(this);
        	//增加节点
        	if(node.id == 0){
        		// 新增节点
        		//parentId:新增节点的父节点id
        		//name:新增节点的文本
        		
        		$.post("/content/category/create",{parentId:node.parentId,name:node.text},function(data){
        			//新增成功
        			if(data.status == 200){
        				//更新节点
        				_tree.tree("update",{
            				target : node.target,
            				id : data.data.id
            			});
        			//新增失败
        			}else{
        				$.messager.alert('提示','创建'+node.text+' 分类失败!');
        			}
        		});
        	//更新节点
        	}else{
        		$.post("/content/category/update",{id:node.id,name:node.text});
        	}
        }
	});
});
//处理点击菜单事件
function menuHandler(item){
	//获取树
	var tree = $("#contentCategory");
	//获取选中的节点
	var node = tree.tree("getSelected");
	//添加选项
	if(item.name === "add"){
		//在被点击的节点下追加一个子节点
		tree.tree('append', {
            parent: (node?node.target:null),//被添加的子节点的父节点
            data: [{
                text: '新建分类',//节点内容
                id : 0,			//节点id
                parentId : node.id//新建的节点的父节点的id
            }]
        }); 
		//找到id为0的节点
		var _node = tree.tree('find',0);
		//选中id为0的节点并开始编辑
		tree.tree("select",_node.target).tree('beginEdit',_node.target);
	//重命名选项
	}else if(item.name === "rename"){
		tree.tree('beginEdit',node.target);
	//删除选项
	}else if(item.name === "delete"){
		//获取树中被选中的节点
    	var node = $("#contentCategory").tree("getSelected");
    	//判断选中的节点是否存在,或者是否是叶子节点
    	if(!$("#contentCategory").tree("isLeaf",node.target)){
    		$.messager.alert('提示','只能删除叶子节点!');
    		return ;
    	}
		$.messager.confirm('确认','确定删除名为 '+node.text+' 的分类吗？',function(r){
			if(r){
				$.post("/content/category/delete/",{id:node.id},function(){
					tree.tree("remove",node.target);
				});	
			}
		});
	}
}
</script>