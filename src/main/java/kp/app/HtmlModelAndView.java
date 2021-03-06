package kp.app;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Simple static implementation for a HTML page that has placeholder variables
 * for {@link ViewModelData.Attr}.
 * 
 * @author broxp
 */
public class HtmlModelAndView extends ModelAndView {
	private static final Log log = LogFactory.getLog(HtmlModelAndView.class);

	public HtmlModelAndView(String res, ViewModelData modelData) {
		super(createView(new ClassPathResource(res), modelData), "model", modelData);
	}

	/** Returns a view rendering the given HTML. */
	private static View createView(Resource resource, ViewModelData modelData) {
		return new View() {
			@Override
			public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse resp)
					throws Exception {
				try {
					resp.setContentType(getContentType());

					InputStream inputStream = resource.getInputStream();
					InputStreamReader in = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
					String string = FileCopyUtils.copyToString(in);

					for (ViewModelData.Attr attr : ViewModelData.Attr.values()) {
						Object object = ViewModelData.class.getField(attr.name()).get(modelData);
						string = string.replace("${" + attr + "}", String.valueOf(object));
					}

					OutputStreamWriter out = new OutputStreamWriter(resp.getOutputStream());
					FileCopyUtils.copy(string, out);
				} catch (Exception e) {
					log.error("", e);
					throw e;
				}
			}

			@Override
			public String getContentType() {
				return MediaType.TEXT_HTML_VALUE;
			}
		};
	}
}