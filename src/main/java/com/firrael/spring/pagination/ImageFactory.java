package com.firrael.spring.pagination;

import java.io.InputStream;

import com.firrael.spring.data.storage.Image;
import com.mongodb.gridfs.GridFSDBFile;

public class ImageFactory {
	public static Image buildImage(GridFSDBFile source) {
		
		String id = source.getId().toString();
		String fileName = source.getFilename();
		InputStream content = source.getInputStream();
		
		Image image = new Image(id, fileName, content);
		
		return image;
	}
}
