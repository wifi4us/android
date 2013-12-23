<?PHP
    class xml{
        private $xml;
      
        public function __construct() {
            $this->xml = new XMLWriter();
            //输出方式，也可以设置为某个xml文件地址，直接输出成文件
          	$this->xml->openUri("php://output");
          	$this->xml->setIndentString('  ');
          	$this->xml->setIndent(true);
            $this->xml->startDocument('1.0', 'utf-8');
            //根结点
            $this->xml->startElement('result');
        }

        public function __destruct() {
            $this->xml->endElement(); //result
            $this->xml->endDocument();
          //$this->xml->flush();
        }

        public function make_xml($data, $attribute_array = array()) {
            foreach ($data as $key => $value) {
                (is_numeric($key)) ? $key='item' : $key ;
                $this->xml->startElement($key);
                
                if (isset($attribute_array[$key]) && is_array($attribute_array[$key])) {
                    foreach ($attribute_array[$key] as $akey => $aval) {
                        //设置属性值
                        $this->xml->writeAttribute($akey, $aval);
                    }
                }
                if (is_array($value) || is_object($value)) {
                    $this->make_xml($value, $attribute_array);  
                } else{
                    //设置内容
                    $this->xml->text($value);    
                }
                $this->xml->endElement(); // $key
            }
        }
    };
?>