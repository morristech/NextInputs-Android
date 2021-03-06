package com.github.yoojia.inputs.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.yoojia.inputs.AndroidNextInputs;
import com.github.yoojia.inputs.LazyLoaders;
import com.github.yoojia.inputs.Scheme;
import com.github.yoojia.inputs.StaticScheme;
import com.github.yoojia.inputs.ValueScheme;
import com.github.yoojia.inputs.Verifier;
import com.github.yoojia.inputs.WidgetAccess;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AndroidNextInputs inputs = new AndroidNextInputs();
        // Inputs会保存各个规则，如果Inputs被Activity缓存，onCreate会多次调用。需要注意清除。
        inputs.clear();

        final WidgetAccess access = new WidgetAccess(this);
        // 一、流式API
        inputs  // 必选，手机号
                .add(access.findEditText(R.id.form_field_1))
                .with(StaticScheme.Required(), StaticScheme.ChineseMobile())
                // 信用卡
                .add(access.findEditText(R.id.form_field_2))
                .with(StaticScheme.BankCard());
        // 二、标准API
        // 必选，数字，最大20字符
        inputs.add(access.findEditText(R.id.form_field_3), StaticScheme.Required(), StaticScheme.Digits(), ValueScheme.MaxLength(20));
        // 必选，邮件
        inputs.add(access.findEditText(R.id.form_field_4), StaticScheme.Required(), StaticScheme.Email());
        // 必选，与邮件相同
        final LazyLoaders loader = new LazyLoaders(this);
        inputs.add(access.findEditText(R.id.form_field_5), ValueScheme.Required(), ValueScheme.EqualsTo(loader.fromEditText(R.id.form_field_4)));
        // Host
        inputs.add(access.findEditText(R.id.form_field_6), StaticScheme.Host());
        // URL
        inputs.add(access.findEditText(R.id.form_field_6), StaticScheme.URL());
        // MaxLength
        inputs.add(access.findEditText(R.id.form_field_7), ValueScheme.MaxLength(5));
        // MinLength
        inputs.add(access.findEditText(R.id.form_field_8), ValueScheme.MinLength(4));
        // RangeLength
        inputs.add(access.findEditText(R.id.form_field_9), ValueScheme.RangeLength(4, 8));
        // Not Blank
        inputs.add(access.findEditText(R.id.form_field_10), StaticScheme.NotBlank());
        // Numeric
        inputs.add(access.findEditText(R.id.form_field_11), StaticScheme.Numeric());
        // MaxValue
        inputs.add(access.findEditText(R.id.form_field_12), ValueScheme.MaxValue(100));
        // MinValue
        inputs.add(access.findEditText(R.id.form_field_13), ValueScheme.MinValue(20));
        // RangeValue
        inputs.add(access.findEditText(R.id.form_field_14), ValueScheme.RangeValue(18, 30));

        // 自定义校验规则:
        // 1. 跟上面一样，指定需要校验的View；
        // 2. 通过Scheme.create方法，创建Verifier校验器，校验器实现具体的校验规则；
        Verifier myVerifier = new Verifier() {
            @Override
            public boolean perform(String rawInput) throws Exception {
                return "对输入内容进行自定义规则校验".equals(rawInput);
            }
        };
        inputs.add(access.findTextView(R.id.form_field_14), Scheme.create(myVerifier).msg("自动"));

        final Button submit = (Button) findViewById(R.id.form_commit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean passed = inputs.test();
                if (passed) {
                    submit.setText("校验通过");
                }else{
                    submit.setText("校验失败");
                    access.findEditText(R.id.form_field_1).inputView.setText("12222");
                    access.findEditText(R.id.form_field_1).inputView.setError(null);
                }
            }
        });
    }
}
