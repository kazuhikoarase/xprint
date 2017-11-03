package com.d_project.xprint.core;

import java.awt.geom.Dimension2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.d_project.xprint.scripting.XPrintContextImpl;

/**
 * XScript
 * @author Kazuhiko Arase
 */
public class XScript extends XNode {

	private XGraphicsPlayer player;
    
    private Dimension2D contentSize;

    public XScript() {
        super(NodeNames.SCRIPT_NODE);
    }

    public Dimension2D getContentSize(XNodeContext context) {
    	
    	if (contentSize == null) {
    		contentSize = createContentSize(context);
    	}

        return contentSize;
    }

    public void paint(XNodeContext context, IXGraphics g) {

    	if (player == null) {
    		player = createPlayer(context);
    	}

		player.play(g);

		return;
    }

    private Dimension2D createContentSize(XNodeContext context) {

        final double[] size = new double[2];

        execSrc(context, new PostExecute() {
			
			public void execute(ScriptEngine engine)
			throws Exception {

				Object func = engine.get("getContentSize");

		        if (func != null) {
		        	engine.put("__printContext__",
		        		new XPrintContextImpl(XScript.this, null) );
		        	size[0] = ((Number)engine.eval("getContentSize(__printContext__)[0]") ).doubleValue();
		        	size[1] = ((Number)engine.eval("getContentSize(__printContext__)[1]") ).doubleValue();
		        }
			}
		});

        return new Size2D(size[0], size[1]);
    }

    private XGraphicsPlayer createPlayer(XNodeContext context) {
    	
    	final XGraphicsPlayer player = new XGraphicsPlayer();
    	
    	execSrc(context, new PostExecute() {
			
			public void execute(ScriptEngine engine)
			throws Exception {

				Object func = engine.get("paint");
	            
		        if (func != null) {
		        	engine.put("__printContext__", new XPrintContextImpl(
		        		XScript.this, player.getGraphics() ) );
		        	engine.eval("paint(__printContext__)");
		        }
			}
		});

        return player;
    }

  private ScriptEngine createScriptEngine() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("ECMAScript");
    String filename = "Bridge.js";
    Reader in = new BufferedReader(new InputStreamReader(
        getClass().getResourceAsStream(filename), "Utf-8") );
    try {
      engine.put(ScriptEngine.FILENAME, filename);
      engine.eval(in);
    } finally {
      in.close();
    }
    return engine;
  }

  private void execSrc(XNodeContext context, PostExecute postExecute) {
    if (!Config.getInstance().scriptEnabled() ) {
      throw new SecurityException("script not enabled.");
    }
    try {
      String src = getAttribute(AttributeKeys.SRC);
      ScriptEngine engine = createScriptEngine();
      Reader in = new BufferedReader(new InputStreamReader(
        context.getResourceAsStream(src), "Utf-8") );
      try {
        engine.put(ScriptEngine.FILENAME, src);
        engine.eval(in);
        postExecute.execute(engine);
      } finally {
        in.close();
      }
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

    private static interface PostExecute {
    	void execute(ScriptEngine engine) throws Exception;
    }
}
