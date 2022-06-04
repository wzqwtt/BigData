#!/bin/bash

GEN_CONFIG_HOME=/opt/module/datax/script

python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t activity_info
python $GEN_CONFIG_HOME//gen_import_config.py -d gmall -t activity_rule
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_category1
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_category2
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_category3
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_dic
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_province
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_region
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t base_trademark
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t cart_info
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t coupon_info
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t sku_attr_value
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t sku_info
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t sku_sale_attr_value
python $GEN_CONFIG_HOME/gen_import_config.py -d gmall -t spu_info