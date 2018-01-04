package com.philthi.bartender;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Bartender extends ApplicationAdapter {
	private int grid_size = 256;
	private float scale = 1.0f;
	SpriteBatch batch;
	Bar bar;
	Order order;
	Map<String, Texture> textures;
	float timeToSpawn;
	ArrayList<Customer> waitingCustomers;

	public Bartender(float scl, int gridSize) {
		scale = scl;
		grid_size = gridSize;
		order = new Order();
		waitingCustomers = new ArrayList<Customer>();
	}

	public void create () {
		InputStream is = Gdx.files.internal("layout.json").read();
		try {
			bar = Bar.load(IOUtils.toString(is));
		} catch(Exception e) {
			Gdx.app.error("BARTENDER", "Exception loading bar layout: " + e.getMessage());
		}
		textures = new HashMap<String, Texture>();
		for(Bar.BarEntity e: bar.layout.entities) {
			textures.put(e.texture, new Texture(Gdx.files.internal(e.texture)));
		}

		textures.put("order_beer", new Texture(Gdx.files.internal("order_beer.png")));
		textures.put("order_cider", new Texture(Gdx.files.internal("order_cider.png")));
		textures.put("order_dirty_glass", new Texture(Gdx.files.internal("order_dirty_glass.png")));
		textures.put("order_clean_glass", new Texture(Gdx.files.internal("order_clean_glass.png")));
		textures.put("order_spirits", new Texture(Gdx.files.internal("order_spirits.png")));
		textures.put("order_juice", new Texture(Gdx.files.internal("order_juice.png")));
		textures.put("order_fruit", new Texture(Gdx.files.internal("order_fruit.png")));
		textures.put("order_fruit_juice", new Texture(Gdx.files.internal("order_fruit_juice.png")));
		textures.put("order_fruit_spirits", new Texture(Gdx.files.internal("order_fruit_spirits.png")));
		textures.put("order_spirits_juice", new Texture(Gdx.files.internal("order_spirits_juice.png")));
		textures.put("order_spirits_juice_fruit", new Texture(Gdx.files.internal("order_spirits_juice_fruit.png")));
		textures.put("order_unknown", new Texture(Gdx.files.internal("order_unknown.png")));

		textures.put("customer", new Texture(Gdx.files.internal("customer.jpg")));
		batch = new SpriteBatch();
	}

	@Override
	public void render () {
		if(Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			Touchable touchedEntity = touchedWhat(touchPos);
			if(touchedEntity != null) {
				touchedEntity.Activate(order, waitingCustomers);
			}
		}
		spawnCustomer();
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for(Bar.BarEntity e: bar.layout.entities){
			for(Bar.BarEntityPosition p: e.positions) {
				batch.draw(textures.get(e.texture), p.x * grid_size * scale, p.y * grid_size * scale, grid_size * scale, grid_size * scale);
			}
		}
		for(Customer c: waitingCustomers){
			float renderX = c.x * grid_size * scale;
			float renderY = c.y * grid_size * scale;
			batch.draw(textures.get("customer"), renderX, renderY, grid_size * scale, grid_size * scale);
			batch.draw(textures.get(c.order.getType()), renderX+8, renderY+40, 64 * scale, 64 * scale);
		}
		batch.draw(textures.get(order.getType()), Gdx.graphics.getWidth() - (192 * scale), Gdx.graphics.getHeight() - (192 * scale), (192 * scale), (192 * scale));

		batch.end();
	}

	private void spawnCustomer() {
		if ( timeToSpawn > 0) {
			timeToSpawn -= Gdx.graphics.getRawDeltaTime();
			return;
		}
		Gdx.app.log("BARTENDER", "Spawning customer");
		timeToSpawn = 15;

		for(Bar.BarEntity e: bar.layout.entities){
			if(!e.type.equals("customer_area")){
				continue;
			}
			for(Bar.BarEntityPosition p: e.positions){
				boolean customerHere = false;
				Gdx.app.log("BARTENDER", "checking for customer at (" + p.x + ", " + p.y + ")");
				for(Customer c: waitingCustomers){
					if(c.x == p.x && c.y == p.y){
						customerHere = true;
					}
				}
				if(!customerHere){
					Gdx.app.log("BARTENDER", "Spawning customer at: (" + p.x + ", " + p.y + ")");
					//found a free customer area, spawn here
					Order o = new Order();
					o.addIngredient(Ingredient.Factory("clean_glass"));
					o.addIngredient(Ingredient.Factory("beer"));
					waitingCustomers.add(new Customer(o, p.x, p.y));
					return;
				}

			}
		}

	}

	@Override
	public void dispose () {
		for(Texture t: textures.values()){
			t.dispose();
		}
		batch.dispose();
	}

	private Touchable touchedWhat(Vector3 pos) {
		pos.y = Gdx.graphics.getHeight() - pos.y;
		for(Customer c: waitingCustomers){
			float leftX = c.x * grid_size * scale;
			float rightX = (c.x + 1) * grid_size * scale;
			float topY = (c.y + 1) * grid_size * scale;
			float bottomY = c.y * grid_size * scale;
			if(pos.x >= leftX && pos.x <= rightX && pos.y <= topY && pos.y >= bottomY) {
				return c;
			}
		}
		for(Bar.BarEntity e: bar.layout.entities){
			for(Bar.BarEntityPosition p: e.positions) {
				float leftX = p.x * grid_size * scale;
				float rightX = (p.x + 1) * grid_size * scale;
				float topY = (p.y + 1) * grid_size * scale;
				float bottomY = p.y * grid_size * scale;
				if(pos.x >= leftX && pos.x <= rightX && pos.y <= topY && pos.y >= bottomY) {
					return e;
				}
			}
		}
		return null;
	}
}
