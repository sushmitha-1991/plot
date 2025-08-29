/**
 * 
 * @author 
 * @version 
 */
package com.dsp.plot.response;

import com.dsp.plot.config.XSSConfig.NoEscape;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
	private int statusCode;
	private boolean success;
	@NoEscape
	private String message;
	private T data;
}
