package com.firrael.spring.pagination;

import com.firrael.spring.data.storage.Image;
import com.mongodb.gridfs.GridFSDBFile;

public class ImageFactory {
	public static Image buildImage(GridFSDBFile source) {
		
		Image image = new Image(source.getFilename(), source.getInputStream());
		
		return image;
	}
}
