package com.huangss.goods.admin.book.web.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;

import com.huangss.goods.Category.Service.CategoryService;
import com.huangss.goods.Category.domain.Category;
import com.huangss.goods.book.Service.BookService;
import com.huangss.goods.book.domain.Book;

/**
 * 完成添加图片
 * 	文件上传的三个要素：
 * 		1>post方式发送表单
 * 		2>表单的encType=multipart/form-data
 * 		3>使用<input type="file" name="file">控件
 * @author 黄松松
 *
 */
public class AdminAddBookServlet extends HttpServlet {

	private BookService service = new BookService();
	/**
	 * 完成添加图书
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//1.创建一个DiskFileItemFactory
		DiskFileItemFactory factory=new DiskFileItemFactory();
	   //2.创建ServletFileUpload类					
		ServletFileUpload upload=new ServletFileUpload(factory);
		//设置上传文件的大小
		upload.setFileSizeMax(1024 * 10 * 10 * 10);
	   //3.解析所有上传数据
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			request.setAttribute("msg", "文件过大");
			//回现所有一级分类
			request.setAttribute("parents", new CategoryService().loadParent());
			request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
				.forward(request, response);
			return;
		}
		//装所有非上传组件的内容
		Map<String, Object> map = new HashMap<String, Object>(); 
		Book book = new Book();
		//区分第一张和第二张图片
		int count =0;
		String image_w =null;
		String image_b =null;
		//判断是否是上传表单
		if(upload.isMultipartContent(request)){
			for(FileItem item : items){
				if(!item.isFormField()){//是上传组件
					count++;
					//得到第一张图片的上传名
					String filename = item.getName();
					System.out.println("1 =" +filename);
					// 截取文件名，因为部分浏览器上传的绝对路径
					int index = filename.lastIndexOf("\\");
					if(index != -1) {
						filename = filename.substring(index + 1);
					}
					System.out.println(filename);
					//判断上传的是否是图片的格式
					if(checkPicture(filename)){
						//防止出现重名，给文件名添加一个随机的uuid
						filename = CommonUtils.uuid() + "_" + filename;
						//这种图片的存储位置
						String realpath = this.getServletContext().getRealPath("/book_img");
						//创建目标文件
						File file = new File(realpath, filename);
						try {
							item.write(file);//将目标文件存储到指定位置，再删除临时文件
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}else{
						request.setAttribute("msg", "图片格式不对，请重新上传");
						//回现所有一级分类
						request.setAttribute("parents", new CategoryService().loadParent());
						request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
							.forward(request, response);
						return;
					}
					
					//设置保存在数据库图片的路径
					if(count == 1){
						image_w = "book_img/" + filename;
					}else if(count == 2){
						image_b = "book_img/" + filename;
					}
					
				}else{//普通组件
					map.put(item.getFieldName(), item.getString("utf-8"));
				}
			}
			//将普通表单字段的值封装到Book中(其中并没有图片的值)
			book = CommonUtils.toBean(map, Book.class);
			Category category = CommonUtils.toBean(map, Category.class); 
			book.setCategory(category);
			book.setBid(CommonUtils.uuid());
			book.setImage_w(image_w);
			book.setImage_b(image_b);
			
			//调用service方法完成添加
			service.addBook(book);
			// 保存成功信息转发到msg.jsp
			request.setAttribute("msg", "添加图书成功！");
			request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request, response);
		
		}else{
			request.setAttribute("msg", "操作错误呢");
			//回现所有一级分类
			request.setAttribute("parents", new CategoryService().loadParent());
			request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
				.forward(request, response);
			return;
		}
	}
	
	/**
	 * 验证图片的格式
	 * @param filename
	 * @return
	 */
	private boolean checkPicture(String filename) {
		if(!filename.toLowerCase().endsWith(".png") && !filename.toLowerCase().endsWith("jpg")){
			return false;
		}
		return true;
	}
}
