/**
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.nohttp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created in Jul 31, 2015 3:19:45 PM
 * 
 * @author YOLANDA
 */
public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		findViewById(R.id.btnTestDownload).setOnClickListener(this);
		findViewById(R.id.btnTestRequest).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnTestDownload) {
			Intent intent = new Intent(this, DownloadActivity.class);
			startActivity(intent);
		}
		if (v.getId() == R.id.btnTestRequest) {
			Intent intent = new Intent(this, NoHttpActivity.class);
			startActivity(intent);
		}
	}

}
