package com.dyo.d3quests;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyo.d3quests.model.Hero;

public class HeroesArrayAdapter extends ArrayAdapter<Hero> {

	private final Context context;
	private final List<Hero> heroes;

	public HeroesArrayAdapter(Context context, int resource, List<Hero> objects) {
		super(context, resource, objects);
		this.context = context;
		this.heroes = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ImageView heroThumbnail;
		TextView heroName;
		TextView heroDetails;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.heroes_list_item, parent, false);
			heroThumbnail = (ImageView) convertView.findViewById(R.id.hero_thumbnail);
			heroName = (TextView) convertView.findViewById(R.id.hero_name);
			heroDetails = (TextView) convertView.findViewById(R.id.hero_detail);
			convertView.setTag(new ViewHolder(heroThumbnail, heroName, heroDetails));
		} else {
			ViewHolder holder = (ViewHolder) convertView.getTag();
			heroThumbnail = holder.heroThumbnail;
			heroName = holder.heroName;
			heroDetails = holder.heroDetails;
		}

		Hero hero = heroes.get(position);

		heroName.setText(hero.getName());
		heroDetails.setText(hero.getFormattedLevelAndClass());
		heroThumbnail.setImageResource(imageByGenderAndClass(hero));
		return convertView;
	}

	int imageByGenderAndClass(Hero hero) {
		if (hero.getGender() == 0) {
			// Hero is male.
			if (hero.getD3class().equals("barbarian")) {
				return R.drawable.barb_male;
			} else if (hero.getD3class().equals("crusader")) {
				return R.drawable.crusader_male;
			} else if (hero.getD3class().equals("demon-hunter")) {
				return R.drawable.dh_male;
			}else if (hero.getD3class().equals("monk")) {
				return R.drawable.monk_male;
			} else if (hero.getD3class().equals("witch-doctor")) {
				return R.drawable.wd_male;
			}else if (hero.getD3class().equals("wizard")) {
				return R.drawable.wiz_male;
			}
		} else {
			// Hero is female.
			if (hero.getD3class().equals("barbarian")) {
				return R.drawable.barb_female;
			} else if (hero.getD3class().equals("crusader")) {
				return R.drawable.crusader_female;
			} else if (hero.getD3class().equals("demon-hunter")) {
				return R.drawable.dh_female;
			}else if (hero.getD3class().equals("monk")) {
				return R.drawable.monk_female;
			} else if (hero.getD3class().equals("witch-doctor")) {
				return R.drawable.wd_female;
			}else if (hero.getD3class().equals("wizard")) {
				return R.drawable.wiz_female;
			}
		}

		// Unknown class.
		return R.drawable.ab_solid_ros;
	}

	static class ViewHolder {
		ImageView heroThumbnail;
		TextView heroName;
		TextView heroDetails;

		public ViewHolder(ImageView heroThumbnail, TextView heroName, TextView heroDetails) {
			this.heroThumbnail = heroThumbnail;
			this.heroName = heroName;
			this.heroDetails = heroDetails;
		}
	}
}
