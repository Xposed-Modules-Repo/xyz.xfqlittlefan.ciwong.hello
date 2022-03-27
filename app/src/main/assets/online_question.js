// 加载组件
function onlineDoWorkLoadComponent(Vue) {
    // 小题整合
    Vue.component('question', { template: '\
        <question-choice            v-if="question.qtype==1"                    :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index"></question-choice>\
        <question-multiple-choice   v-if="question.qtype==2"                    :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index"></question-multiple-choice>\
        <question-filling           v-if="question.qtype==3"                    :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index"></question-filling>\
        <question-reading           v-if="question.qtype==4||question.qtype==5" :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index" :client="client"></question-reading>\
        <question-shortanswer-for-language       v-if="parentqtype==10" :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index" :client="client"></question-shortanswer-for-language>\
        <question-shortanswer       v-if="parentqtype!=10 && (question.qtype==6||question.qtype==7)" :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index" :client="client"></question-shortanswer>\
        <question-judge             v-if="question.qtype==8"                    :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index"></question-judge>\
        <question-error-correction  v-if="question.qtype==9" :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index" :client="client"></question-error-correction>\
        <question-language-plait    v-if="question.qtype==10" :question="question" :questionnum="questionnum" :showrefanswers="showrefanswers" :hidescore="hidescore" :parentqtype="parentqtype" :index="index" :client="client"></question-language-plait>\
    ', props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'index', 'parentqtype', 'client'] })
    // 附件集合
    Vue.component('attachments', { template: '\
        <div v-for="data in attachments">\
            <attachment :data="data"></attachment>\
        </div>\
        ', props: ['attachments'] });
    // 单个附件[拓展组件]
    Vue.component('attachment', { template: '\
        <div class="clearfix" v-if="data.file_url">\
            <div class="img-box mt30" v-if="data.file_type == 1">\
                <p v-if="data.title" v-html="data.title"></p>\
                <img onclick="hammerClass(this)" :src="data.file_url" alt="" />\
            </div>\
            <div class="audioBox mt30 clearfix" v-if="data.file_type == 2">\
                <div class="audioBtn"></div>\
                <div class="taskBoxLinks"><h3><i></i></h3></div>\
                    <div class="timeBox"><em class="currentTime"></em><strong class="duration"></strong></div>\
                <div class="loadError fl ml20"></div>\
                <audio :src="data.file_url" preload="auto"></audio>\
            </div>\
            <div class="video pr mt30" v-if="data.file_type == 3">\
                <video v-if="data.file_url.indexOf(\'letv://\')>-1" :src="data.file_url | getIframeVideoSrc" controls="controls" preload="auto" x5-video-player-fullscreen="true" webkit-playsinline="true" x-webkit-airplay="true" playsinline="true" x5-playsinline></video>\
                <video v-else :src="data.file_url" controls="controls" preload="auto" x5-video-player-fullscreen="true" webkit-playsinline="true" x-webkit-airplay="true" playsinline="true" x5-playsinline></video>\
            </div>\
        </div>\
        ', props: ['data'] });
    // 参考信息,包括答案和试题解析
    Vue.component('refinfo',{template:'\
        <div class="refanswers on">\
            <div class="refanswers-head fs30 p30 col-999896 pt30" v-if="showrefanswers">参考答案</div>\
            <div class="refanswers-body" v-if="showrefanswers">\
                <div class="p30" v-if="question.ref_info && question.ref_info.answers && question.ref_info.answers.length>0">\
                    <div class="green">\
                        <div class="inline" v-if="question.qtype==1 || question.qtype==2" v-for="val in question.ref_info.answers">{{val.sid | fromCharCode}}</div>\
                        <div class="inline" v-if="question.qtype==3 || (parentqtype==10 && question.qtype==6)" v-for="val in question.ref_info.answers">{{$index!=0? \'；\':\'\'}}<div class="inline" v-html="val | fillingRefAnswer"></div></div>\
                        <div class="inline" v-if="parentqtype!=10 && (question.qtype==6 || question.qtype==7)" v-for="val in question.ref_info.answers" v-html="val"></div>\
                        <div class="inline" v-if="question.qtype==8" v-for="val in question.ref_info.answers">{{val==\'1\'?\'正确\':\'错误\'}}</div>\
                    </div>\
                </div>\
                <div class="mt30 plr30 bdt1-f1f0ec" v-for="explain in question.explains" v-if="explain.context || (explain.attachments && explain.attachments.length>0)">\
                    <div class="fs30 col-999896 pt30" >试题解析</div>\
                    <div class="vertical-middle green mt20" >\
                        <div v-html="explain.context"></div>\
                        <attachments :attachments="explain.attachments"></attachments>\
                    </div>\
                </div>\
            </div>\
        </div>\
        ',props:['question','showrefanswers','parentqtype']
    })
    // 短文改错参考信息,包括答案和试题解析
    Vue.component('refinfo2',{template:'\
        <div>\
            <div class="refanswers mt30 on" v-if="showrefanswers && question.explains && question.explains.length>0">\
                <div class="refanswers-head fs30 p30 col-999896 pt30">综合讲解</div>\
                <div class="refanswers-body">\
                    <div class="p30" v-for="explain in question.explains">\
                        <div class="vertical-middle green mt20">\
                            <div v-html="explain.context"></div>\
                            <attachments :attachments="explain.attachments"></attachments>\
                        </div>\
                    </div>\
                </div>\
            </div>\
            <div class="refanswers on" data-qtype="9">\
                <div class="refanswers-head fs30 p30 col-999896 pt30" v-if="showrefanswers">参考答案</div>\
                <div class="refanswers-body lh54 plr30" v-if="showrefanswers">\
                    <div class="e-c-q-box inline" v-for="(index,item) in question.refAnswerArr">\
                        <div class="html-br" v-if="item.word==\'<br/>\'"></div>\
                        <div v-if="item.word && item.word!=\'<br/>\'" class="e-c-q" :class="item.answer && item.answer.add? \'word-add\':item.answer && item.answer.instead? \'word-update\':item.answer && item.answer.isDelete? \'word-del\':\'\'" :class="item.assess===0?\'ecq-yellow\':item.assess===2?\'ecq-red\':\'ecq-green\'">\
                            <i></i>\
                            <div class="word" v-html="item.word"></div>\
                            <div class="user-answer" v-html="item.answer && (item.answer.add || item.answer.instead)" v-if="item.answer && (item.answer.add || item.answer.instead)"></div>\
                        </div> \
                    </div>\
                    <div class="eq-sub-question" v-for="subQuestion in question.children">\
                        <div class="mt30 bdt1-f1f0ec" v-if="subQuestion.ref_info && subQuestion.ref_info.answers && subQuestion.ref_info.answers.length>0">\
                            <div>\
                                <div class="fs30 col-999896 pt30">{{$index+1}}、参考答案</div>\
                                <div class="vertical-middle green mt20">\
                                    <div v-if="subQuestion.ref_info.answers[0].add">\
                                        在 <span v-html="question.refAnswerArr[subQuestion.ref_info.answers[0].index].word | removeP"></span> 前面添加 <span v-html="subQuestion.ref_info.answers[0].add"></span>\
                                    </div>\
                                    <div v-if="subQuestion.ref_info.answers[0].instead">\
                                        把 <span v-html="question.refAnswerArr[subQuestion.ref_info.answers[0].index].word | removeP"></span> 改成 <span v-html="subQuestion.ref_info.answers[0].instead"></span>\
                                    </div>\
                                    <div v-if="subQuestion.ref_info.answers[0].isDelete">\
                                        删除 <span v-html="question.refAnswerArr[subQuestion.ref_info.answers[0].index].word | removeP"></span>\
                                    </div>\
                                </div>\
                            </div>\
                            <div v-for="explain in subQuestion.explains">\
                                <div class="fs30 col-999896 pt30">试题解析</div>\
                                <div class="vertical-middle green mt20">\
                                    <div v-html="explain.context"></div>\
                                    <attachments :attachments="explain.attachments"></attachments>\
                                </div>\
                            </div>\
                        </div>\
                    </div>\
                </div>\
            </div>\
        </div>\
        ',props:['question','showrefanswers','parentqtype']
    })
    // 序号线
    Vue.component('sortline', { template: '\
        <div class="sortContent mt30">\
            <div class="serial_num"><span class="green">{{sort || 1}}</span>\
                <span class="col-999896">/</span>\
                <span class="col-999896 fs-24">{{totalnum || 1}}</span>\
            </div>\
            <div class="part_line"></div>\
        </div>\
    ', props: ['sort', 'totalnum'] });
    // 1单选题
    Vue.component('question-choice', { template: '\
        <div>\
            <div class="mlr30 pb30">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <span v-if="parentqtype || question.sort">{{parentqtype?index:question.sort}}.</span>\
                    <div class="inline" v-html="question.stem"></div>\
                </div>\
                <attachments :attachments="question.attachments"></attachments>\
                <div class="choAnw mt40" v-for="(index,option) in question.options">\
                    <div class="fs-80" @click="doWork(index)">\
                        <span class="option" :data-id="option.id" :class="option.assess?\'op-green\':\'\'">{{$index | fromCharCode}}</span><div class="choTit inline" v-html="option.stem"></div>\
                    </div>\
                    <attachments :attachments="option.attachments"></attachments>\
                </div>\
            </div>\
            <refinfo :question="question" :showrefanswers="showrefanswers" :parentqtype="parentqtype"></refinfo>\
        </div>\
        ',
            props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index'],
            methods: {
                doWork: function(index) {
                    var self = this
                    clearTimeout(window.questionTimer)
                    window.jumpFlag = false
                    this.question.userAnswer = local.doWork(this.question, index)
                    this.question = methods.setOptionsIcon(this.question);
                    window.vue.updateQuestion(self.question)

                }
            }
        }
    );
    // 2多选题
    Vue.component('question-multiple-choice', { template: '\
        <div class=" multipleChoice">\
            <div class="mlr30 pb30">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <span v-if="parentqtype || question.sort">{{parentqtype?index:question.sort}}.</span>\
                    <div class="inline" v-html="question.stem"></div>\
                </div>\
                <attachments :attachments="question.attachments"></attachments>\
                <div class="choAnw mt40" v-for="(index,option) in question.options">\
                    <div class="fs-80" @click="doWork(index)">\
                        <span class="option" :data-id="option.id" :class="option.assess?\'op-green\':\'\'">{{$index | fromCharCode}}</span><div class="choTit inline" v-html="option.stem+\'&nbsp;\'"></div>\
                    </div>\
                    <attachments :attachments="option.attachments"></attachments>\
                </div>\
            </div>\
            <refinfo :question="question" :showrefanswers="showrefanswers" :parentqtype="parentqtype"></refinfo>\
        </div>\
        ', props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index'],
        methods: {
            doWork: function(index) {
                this.question.userAnswer = local.doWork(this.question, index)
                this.question = methods.setOptionsIcon(this.question);
                window.vue.updateQuestion(this.question)
            }
        }
    });
    // 3填空题
    Vue.component('question-filling', { template: '\
        <div>\
            <input class="hide-input" onfocus="this.blur()" />\
            <div class="mlr30 pb30">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <span v-if="parentqtype || question.sort">{{parentqtype?index:question.sort}}.</span>\
                    <div class="inline" v-html="question.stemHtml"></div>\
                    <!--暂时无用<div class="inline" v-for="stem in question.stemArr">\
                    <div class="inline" v-if="$index%2==0" v-html="stem.html"></div>\
                    <input v-if="$index%2!=0" type="text" class="ques" :placeholder="\'(\'+stem.html+\')\'" :data-id="question.version_id" v-model="stem.content" @keyup.enter="doWork($index,1)" @keyup.right="doWork($index,1)" @keyup.left="doWork($index,-1)" @blur="doWork($index)" @focus="filterEvent($index)" />\
                    </div>-->\
                    <p>&nbsp;</p>\
                    <attachments :attachments="question.attachments"></attachments>\
                </div>\
            </div>\
            <refinfo :question="question" :showrefanswers="showrefanswers" :parentqtype="parentqtype"></refinfo>\
            <input class="hide-input" onfocus="this.blur()" />\
        </div>\
        ', props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index'],
        methods: {
            // 暂时无用
            doWork: function(index, direct) {
                try {
                    var inputList = $(this.$el).find('.ques');
                    var optionIndex = (index + 1) / 2 - 1;
                    console.log('content1:', this.question.stemArr[index].content)
                    this.question.options[optionIndex].content = this.question.stemArr[index].content = local.textFilterVal(this.question.stemArr[index].content + '')
                    this.question.userAnswer = local.doWork(this.question, index)
                    this.question = methods.setOptionsIcon(this.question);
                    window.vue.updateQuestion(this.question)
                    console.log('content2:', this.question.stemArr[index].content)
                    if (direct && direct == -1) {
                        if (optionIndex > 0) $(inputList[optionIndex - 1]).focus();
                    } else if (direct && direct == 1) {
                        if (optionIndex < inputList.length - 1) $(inputList[optionIndex + 1]).focus();
                    }
                } catch (error) {
                    console.warn('填空题错误：' + error)
                }
            },
            // 暂时无用
            filterEvent: function(index) {
                var inputList = $(this.$el).find('.ques');
                var optionIndex = (index + 1) / 2 - 1;
                if (vue.currQuestion && vue.currQuestion != this.question.version_id) {
                    $(inputList[optionIndex]).blur()
                    return false;
                }!vue.currQuestion && (vue.currQuestion = this.question.version_id);
            }
        }
    })
    // 4完形填空/5阅读理解
    Vue.component('question-reading', { template: '\
        <div>\
            <div class="problems">\
                <div class="problems-stem"><div class="problems-stem2">\
                    <div class="plr30">\
                        <div class="lh54 mt24">\
                            <div v-if="question.question_description" v-html="question.question_description"></div>\
                            <span v-if="question.sort">{{question.sort}}. </span>\
                            <div class="inline" v-html="question.stem | formatHtml question"></div>\
                        </div>\
                        <attachments :attachments="question.attachments"></attachments>\
                    </div>\
                    <div class="refanswers mt30" v-if="showrefanswers && question.explains && question.explains.length>0">\
                        <div class="refanswers-head fs30 p30 col-999896 pt30">综合讲解</div>\
                        <div class="refanswers-body">\
                            <div class="mt30 plr30" v-for="explain in question.explains">\
                                <div class="vertical-middle green mt20">\
                                    <div v-html="explain.context"></div>\
                                    <attachments :attachments="explain.attachments"></attachments>\
                                </div>\
                            </div>\
                        </div>\
                    </div>\
                </div></div>\
                <div class="problems-question-box">\
                    <div class="touch-part">按住可拖动高度</div>\
                    <div class="sub-question-item" :class="$index==0?\'showTag\':\'\'"  :data-index="$index" v-for="question in question.children">\
                        <question :question="question" :parentqtype="$parent.question.qtype" :showrefanswers="showrefanswers" :index="$index+1" :client="client"></question><p>&nbsp;</p>\
                    </div>\
                </div>\
            </div>\
        </div>\
        ', props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index', 'client']
    })
    // 6/7简答题
    Vue.component('question-shortanswer', { template: '\
        <div>\
            <input class="hide-input" onfocus="this.blur()" />\
            <div class="mlr30 pb30">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <span v-if="parentqtype || question.sort">{{parentqtype?index:question.sort}}.</span>\
                    <div class="inline" v-html="question.stem"></div>\
                </div>\
                <attachments :attachments="question.attachments"></attachments>\
                <div class="anw" v-for="option in question.options">\
                    <div class="shortanswer-content-box" @click="slideToKeyword($index)">\
                        <textarea class="shortanswer-item" :class="option.answerType!=2&&option.answerType!=3?\'on\':\'\'" v-model="option.content" @blur="doWork(option.content)" @focus="filterEvent(0)"></textarea>\
                        <div class="shortanswer-item" :class="option.answerType==2?\'on\':\'\'"><div class="s-photo-box" v-show="option.imgPhotographPath"><img :src="option.imgPhotographPath" alt="" onclick="hammerClass(this)" /><i @click="delphoto($index)">×</i></div></div>\
                        <div class="shortanswer-item" :class="option.answerType==3?\'on\':\'\'"><div class="s-photo-box" v-show="option.imgHandwritingPath"><img :src="option.imgHandwritingPath" alt="" onclick="hammerClass(this)" /><i @click="delphoto($index)">×</i></div></div>\
                    </div>\
                    <div class="shortanswer-btn-box" v-if="!question.is_objective">\
                        <div class="s-btn-item  keyword" @click="doWorkKeyword($index)"  :class="option.answerType!=2&&option.answerType!=3?\'on\':\'\'"><i></i></div>\
                        <div class="s-btn-item photograph" @click="doWorkPhotograph($index)"  :class="option.answerType==2?\'on\':\'\'" v-if="!question.is_objective"><i></i></div>\
                        <div class="s-btn-item handwriting" @click="doWorkHandwriting($index)"  :class="option.answerType==3?\'on\':\'\'" v-if="!question.is_objective && client==22222"><i></i></div>\
                    </div>\
                </div>\
                <!--<div><textarea class="anw" v-model="question.options[0].content" @blur="doWork(question.options[0].content)" @focus="filterEvent(0)"></textarea></div>-->\
            </div>\
            <refinfo :question="question" :showrefanswers="showrefanswers" :parentqtype="parentqtype"></refinfo>\
            <input class="hide-input" onfocus="this.blur()" />\
        </div>\
        ',
        props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index','client'],
        methods: {
            // 键盘作答记录答案
            doWork: function(content) {
                if (this._touchState === 'disabled') return;
                var _this = this
                var _content = local.textFilterVal2(content + '')
                _this.question.options[0].content = _content;
                _this.question.userAnswer = local.doWork(_this.question, _content)
                // _this.question = methods.setOptionsIcon(_this.question);
                window.vue.updateQuestion(_this.question)
                // if(_content != _this.question.options[0].content) {
                // }
                _content==='' && (_this._doingKeyword = false);
            },
            filterEvent: function(index) {
                this._doingKeyword = true;
                var inputList = $(this.$el).find('textarea');
                if (vue.currQuestion && vue.currQuestion != this.question.version_id) {
                    if (this._touchState !== 'disabled') {
                        this._touchState = 'disabled'
                        var _this = this
                        setTimeout(function(){
                            _this._touchState = true
                        }, 500)
                    }
                    inputList.blur()
                    return false;
                };
                !vue.currQuestion && (vue.currQuestion = this.question.version_id);
            },
            // 切换答题方式
            slideAnswerType:function(index,answerType){
                if(answerType!=this.question.options[index].answerType){
                    if(this._doingKeyword || (this.question.options[index].answerType==1 && this.question.options[index].content!='') || (this.question.options[index].answerType==2 && this.question.options[index].imgPhotographUrl!='') || (this.question.options[index].answerType==3 && this.question.options[index].imgHandwritingUrl!='')){
                        $.alert('已有作答内容，请清除后再更换答题方式',3000);
                        this._doingKeyword = false;
                        return false;
                    }
                }
                return true;
            },
            // 点击按钮键盘文本作答
            doWorkKeyword:function(index){
                if(!this.slideAnswerType(index,1)) return false;
                this.question.options[index].answerType = 1;
                this.question.userAnswer = local.doWork(this.question, index)
                window.vue.updateQuestion(this.question)
                this.$nextTick(function(){
                    $(this.$el).find('.shortanswer-item').eq(0).focus();
                });
            },
            // 点击内容区域切换到键盘作答
            slideToKeyword:function(index){
                if(!this.question.options[index].answerType || this.question.options[index].answerType == 1 || this.question.options[index].imgPhotographPath || this.question.options[index].imgHandwritingPath){
                    return false;
                }
                this.doWorkKeyword(index);
                event.stopPropagation();
            },
            // 拍照作答
            doWorkPhotograph:function(index){
                // alert(this._doingKeyword)
                if(!this.slideAnswerType(index,2)) return false;
                this.question.options[index].answerType = 2;
                var fileName = apiData.key+'_'+this.question.version_id+'_'+this.question.sort;
                var _this = this;

                if(location.search.split('?debug=')[1]){
                    _this.question.options[index].imgPhotographPath='./images/pic1.jpg'
                    _this.question.options[index].imgPhotographUrl = './images/pic1.jpg'
                    _this.question.userAnswer = local.doWork(_this.question, index)
                    window.vue.updateQuestion(_this.question)
                    // _this.$nextTick(function(){
                    //     $(this.$el).find('.s-photo-box img')[0].onload = function(){
                    //         if(this.src.indexOf('?time=')==-1) this.src +='?time='+(+new Date());
                    //     }
                    // });
                }
                app.TakePhoto(fileName,function(data){
                    debug  && alert('当前返回数据类型：'+typeof(data))
                    if(!data || !data.photoPath || !data.photoUrl){
                        $.alert('拍照已取消')
                        return false;
                    }
                    _this.question.options[index].imgPhotographPath = data.photoPath+'?time='+(+new Date());   //拍照本地路径
                    _this.question.options[index].imgPhotographUrl = data.photoUrl;     //拍照网络路径
                    _this.question.userAnswer = local.doWork(_this.question,index)
                    window.vue.updateQuestion(_this.question)
                });
            },
            // 手写作答
            doWorkHandwriting:function(index){
                if(!this.slideAnswerType(index,3)) return false;
                this.question.options[index].answerType = 3;
                var fileName = apiData.key+'_'+this.question.version_id+'_'+this.question.sort;
                var _this = this;

                if(location.search.split('?debug=')[1]){
                    _this.question.options[index].imgHandwritingPath='./images/pic2.jpg'
                    _this.question.options[index].imgHandwritingUrl = './images/pic2.jpg'
                    _this.question.userAnswer = local.doWork(_this.question, index)
                    window.vue.updateQuestion(_this.question)
                }

                app.TakePhotoForPad(fileName,function(data){
                    if(!data || !data.photoPath || !data.photoUrl){
                        $.alert('已取消手写')
                        return false;
                    }
                    _this.question.options[index].imgHandwritingPath = data.photoPath+'?time='+(+new Date());      //手写本地路径
                    _this.question.options[index].imgHandwritingUrl = data.photoUrl;        //手写网络路径
                    _this.question.userAnswer = local.doWork(_this.question,index)
                    window.vue.updateQuestion(_this.question)
                });
            },
            // 删除图片
            delphoto:function(index){
                switch(this.question.options[index].answerType){
                    case 2:
                        this.question.options[index].imgPhotographPath='';
                        this.question.options[index].imgPhotographUrl='';
                    case 3:
                        this.question.options[index].imgHandwritingPath='';
                        this.question.options[index].imgHandwritingUrl='';
                    break;
                    default:
                }
                this.question.userAnswer = local.doWork(this.question, '')
                window.vue.updateQuestion(this.question)

                event.stopPropagation()
            },

        }
    })
    // 6简答题 for 语编填空
    Vue.component('question-shortanswer-for-language', { template: '\
        <div>\
            <input class="hide-input" onfocus="this.blur()" />\
            <div class="mlr30 pb30">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <div class="inline" v-html="question.stem"></div>\
                    </div>\
                    <attachments :attachments="question.attachments"></attachments>\
                    <div class="anw shortanswer-to-filling" v-for="option in question.options">\
                    <div class="shortanswer-content-box">\
                        <span v-if="parentqtype || question.sort">{{parentqtype?index:question.sort}}.</span>\
                        <input class="shortanswer-item on"  v-model="option.content" @input="inputEvent" @change="doWork(option.content)" @focus="filterEvent(0)" />\
                    </div>\
                </div>\
            </div>\
            <refinfo :question="question" :showrefanswers="showrefanswers" :parentqtype="parentqtype"></refinfo>\
            <input class="hide-input" onfocus="this.blur()" />\
        </div>\
        ',
        props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index','client'],
        methods: {
            // 键盘作答记录答案
            doWork: function(content) {

                // var _content = local.textFilterVal(content + '')
                var element = $(this.$el).find('input.shortanswer-item').get(0);
                var _content = element.value;
                // $.alert('_content：'+ _content + 'doWork：' + content)

                this.question.options[0].content = _content;
                this.question.userAnswer = local.doWork(this.question, _content)
                // this.question = methods.setOptionsIcon(this.question);
                window.vue.updateQuestion(this.question)
                _content==='' && (this._doingKeyword = false);

            },
            filterEvent: function(index) {
                // $.alert('filterEvent:'+ this.question.options[0].content);
                this._doingKeyword = true;
                var inputList = $(this.$el).find('input');
                if (vue.currQuestion && vue.currQuestion != this.question.version_id) {
                    inputList.blur()
                    return false;
                }
                !vue.currQuestion && (vue.currQuestion = this.question.version_id);
            },
            inputEvent: function() {

                var element = $(this.$el).find('input.shortanswer-item').get(0);
                $(element).width(element.value.length*0.2+'rem')
            }
        },
        created:function(){
            this.$nextTick(function(){
                if(this.question.options && this.question.options && this.question.options.length>0 && this.question.options[0].content!=''){
                    this.inputEvent()
                }
            })
        }
    })
    // 8判断题
    Vue.component('question-judge', {
        template: '\
        <div>\
            <div class="mlr30 pb30">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <span v-if="parentqtype || question.sort">{{parentqtype?index:question.sort}}.</span>\
                    <div class="inline" v-html="question.stem"></div>\
                </div>\
                <attachments :attachments="question.attachments"></attachments>\
                <div class="choAnw mt40" v-for="(index,option) in question.options">\
                    <p class="fs-80 mt20 lh58" @click="doWork(\'1\')"><i class="option-txt" :class="option.content===\'1\'?\'true_curr\':\'true\'"></i><span>正确</span></p>\
                    <p class="fs-80 mt20 lh58" @click="doWork(\'0\')"><i class="option-txt" :class="option.content===\'0\'?\'false_curr\':\'false\'"></i><span>错误</span></p>\
                </div>\
            </div>\
            <refinfo :question="question" :showrefanswers="showrefanswers" :parentqtype="parentqtype"></refinfo>\
        </div>\
        ',
        props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index'],
        methods: {
            doWork: function(assess) {
                clearTimeout(window.questionTimer)
                this.question.options[0].content = assess + '';
                this.question.userAnswer = local.doWork(this.question, assess)
                this.question = methods.setOptionsIcon(this.question);
                window.vue.updateQuestion(this.question)
            }
        }
    })
    // 改错提示层
    Vue.component('error-correction-tips', { template: '\
        <div class="e-c-q-layer">\
            <div class="e-c-q-tips" v-if="item.type == 6">\
                <div class="layer-bg" @click="hideTip"></div>\
                <div class="layer-con">\
                    <div class="inline-block" @click="hideTip">点击单词开始改错</div>\
                </div>\
            </div>\
            <div class="e-c-q-tips" v-if="item.type == 2 && item.answer">\
                <div class="layer-bg" @click="hideTip"></div>\
                <div class="layer-con">\
                    <div class="inline-block" @click="delAnswer">删除此答案</div>\
                </div>\
            </div>\
            <div class="e-c-q-tips" v-if="item.type == 2 && !item.answer">\
                <div class="layer-bg" @click="hideTip"></div>\
                <div class="layer-con">\
                    <div class="inline-block" @click="choiseAction(3)">在单词前添加</div> |\
                    <div class="inline-block" @click="dowork(5)">删除</div> |\
                    <div class="inline-block" @click="choiseAction(4)">改为</div> \
                </div>\
            </div>\
            <div class="e-c-q-add" v-if="item.type == 3">\
                <div class="layer-bg" @click="hideTip"></div>\
                <div class="layer-con">\
                <div class="inline-block">在 <span v-html="filerSign(item.word)"></span> 前添加</div>\
                <input type="text" value="" @keyup="input" @blur="dowork(3)">\
                </div>\
            </div>\
            <div class="e-c-q-update" v-if="item.type == 4">\
                <div class="layer-bg" @click="hideTip"></div>\
                <div class="layer-con">\
                <div class="inline-block">将 <span v-html="filerSign(item.word)"></span> 改为</div>\
                <input type="text" value="" @keyup="input" @blur="dowork(4)">\
                </div>\
            </div>\
        </div>\
        ', props: ['question', 'item'],
        methods: {
            input: function(){
                // var input = $(this.$el).find('input')
                // var val = input.val()
                // input.val(val.substr(0,30))
            },
            // 去除符号和括号内容
            filerSign:function(word){
                return word.replace(/[,.?!:]$|(\([^\)]*?\))/g,'')
            },
            hideTip: function(){
                this.question.stemArr[this.item.sid].type = 0
                console.log(this.question)
            },
            // 用户操作事件 type: [0:未作答 1：已作答 2:弹出提示层 3:在单词前添加 4：修改单词 5:删除]
            choiseAction: function(type){
                this.question.stemArr[this.item.sid].type = type
                this.$nextTick(function(){
                    if(type == 3 || type == 4) {
                        var input = $(this.$el).find('input')
                        input.focus();

                        var layerCon = $(this.$el).find('.layer-con').get(0)
                        local.layerPosition(layerCon)
                        if (!(/(iPhone|iPad)/i.test(navigator.userAgent))) {
                            setTimeout(function(){
                                local.scrollPageTop(layerCon, $('.body-main').get(0))
                            }, 400)
                        }
                    }
                })
            },
            delAnswer: function(){
                this.question.stemArr[this.item.sid].type = 0
                this.question.stemArr[this.item.sid].answer = ''
                var result = this.question.userAnswer
                if (result) {
                    var _this = this
                    result.answers = result.answers.filter(function(answer){
                        return answer.sid !== _this.item.sid
                    })
                    if (result.answers.length == 0) {
                        result = undefined
                    }
                }
                this.question.userAnswer = local.doWork(this.question, result) // 移除指定单词答案
                window.vue.updateQuestion(this.question)
            },
            // 写入答案
            addAnswer: function(answer){
                if (answer) {
                    this.question.stemArr[this.item.sid].answer = answer
                    this.question.stemArr[this.item.sid].type = 1
                    var currAnswer = {
                        sid: this.item.sid,
                        content: JSON.stringify(answer)
                    }
                    var result = this.question.userAnswer
                    if (result) {
                        var _this = this
                        result.answers = result.answers.filter(function(answer){
                            return answer.sid !== _this.item.sid
                        })
                        result.answers.push(currAnswer)
                    } else {
                        result = {
                            versionId: this.question.version_id,
                            answers: [currAnswer]
                        }
                    }
                    this.question.userAnswer = local.doWork(this.question, result)
                    window.vue.updateQuestion(this.question)

                    this.$nextTick(function(){
                        var jDom = $(this.$el).parents('.error-correction')
                        local.optimizeAnswerDisplay(jDom.find('.e-c-question-stem .user-answer'), jDom)
                    })
                }
            },
            dowork: function(type){
                var answer
                if(type == 5){
                    answer = {
                        "isDelete": true,
                        "index": this.item.sid
                    }
                    this.addAnswer(answer)
                    return
                }
                if (this.question.stemArr[this.item.sid].type != 0) {
                    this.$nextTick(function(){
                        var input = $(this.$el).find('input')
                        var val = $tools.trim(input.val())
                        if(val===''){
                            return
                        }
                        // 有作答内容则写入答案记录
                        if(type == 3) {
                            answer = {
                                "add": val,
                                "index": this.item.sid
                            }
                        }else if(type == 4) {
                            answer = {
                                "instead": val,
                                "index": this.item.sid
                            }
                        }
                        this.addAnswer(answer)
                    })
                }
            }
        }
    });
    // 9短文改错 // word-add word-update  word-del
    Vue.component('question-error-correction', { template: '\
        <div class="error-correction">\
            <div class="mlr30 pb30 e-c-question-stem">\
                <div class="lh54 mt24">\
                    <div v-if="question.question_description" v-html="question.question_description"></div>\
                    <div class="e-c-q-box inline" v-for="(index,item) in question.stemArr">\
                        <div class="html-br" v-if="item.word==\'<br/>\'"></div>\
                        <div v-if="item.word && item.word!=\'<br/>\'" class="e-c-q" :class="item.answer && item.answer.add? \'word-add\':item.answer && item.answer.instead? \'word-update\':item.answer && item.answer.isDelete? \'word-del\':\'\'">\
                            <div :class="item.assess===0?\'ecq-yellow\':item.assess===2?\'ecq-red\':\'ecq-green\'">\
                                <i @click="chioseWord(item)"></i>\
                                <div class="word" :class="item.type==2 || item.type==6 || item.type==3 || item.type==4 ? \'on\':\'\'" v-html="item.word" @click="chioseWord(item)"></div>\
                                <div class="user-answer" v-html="item.answer && (item.answer.add || item.answer.instead)" v-if="item.answer && (item.answer.add || item.answer.instead)"></div>\
                                <error-correction-tips :question="question" :item="item"></error-correction-tips> \
                            </div> \
                        </div> \
                    </div>\
                </div>\
                <attachments :attachments="question.attachments"></attachments>\
            </div>\
            <refinfo2 :question="question" :showrefanswers="true" :parentqtype="parentqtype"></refinfo2>\
        </div>\
        ', props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index', 'client'],
        methods: {
            chioseWord: function(item) {
                if ( !item.answer && (this.question.userAnswer && this.question.userAnswer.answers && this.question.userAnswer.answers.length >= 10) ) {
                    $.alert('<div style="white-space: nowrap;">最多可以修改10处哦</div>')
                    return
                }
                this.question.stemArr[item.sid].type = 2
                this.$nextTick(function(){
                    local.layerPosition($(this.$el).find('.e-c-q-box').eq(item.sid).find('.layer-con').get(0))
                })
                console.log(item, this.question)
            }
        },
        created: function(){
            this.$nextTick(function(){
                local.optimizeAnswerDisplay($(this.$el).find('.e-c-question-stem .user-answer'), $(this.$el))
            })
            if (!this.question.userAnswer) {
                var stemArr = this.question.stemArr
                var sid = 13    // 首次进入答题引导所指定的单词索引
                if (stemArr.length > 30) {
                    if(stemArr[sid].word == '<br/>') {
                        sid = sid - 1
                    }
                    stemArr[sid].type = 6
                } else {
                    var sid = Math.floor(stemArr.length/2)
                    if(stemArr[sid].word == '<br/>') {
                        sid = sid - 1
                    }
                    stemArr[sid].type = 6
                }
                this.$nextTick(function(){
                    local.layerPosition($(this.$el).find('.e-c-q').eq(sid).find('.layer-con').get(0))
                })
            }
        }
    })
    // 10语编填空
    Vue.component('question-language-plait', { template: '\
        <div>\
            <div class="problems">\
                <div class="problems-stem"><div class="problems-stem2">\
                    <div class="plr30">\
                        <div class="lh54 mt24">\
                        <div v-if="question.question_description" v-html="question.question_description"></div>\
                            <span v-if="question.sort">{{question.sort}}. </span>\
                            <div class="inline" v-html="question.stem | formatHtml question"></div>\
                        </div>\
                        <attachments :attachments="question.attachments"></attachments>\
                    </div>\
                    <div class="refanswers mt30" v-if="showrefanswers && question.explains && question.explains.length>0">\
                        <div class="refanswers-head fs30 p30 col-999896 pt30">综合讲解</div>\
                        <div class="refanswers-body">\
                            <div class="mt30 plr30" v-for="explain in question.explains">\
                                <div class="vertical-middle green mt20">\
                                    <div v-html="explain.context"></div>\
                                    <attachments :attachments="explain.attachments"></attachments>\
                                </div>\
                            </div>\
                        </div>\
                    </div>\
                </div></div>\
                <div class="problems-question-box">\
                    <div class="touch-part">按住可拖动高度</div>\
                    <div class="sub-question-item" :class="$index==0?\'showTag\':\'\'" :data-index="$index" v-for="question in question.children">\
                        <question :question="question" :parentqtype="$parent.question.qtype" :showrefanswers="showrefanswers" :index="$index+1" :client="client"></question><p>&nbsp;</p>\
                    </div>\
                </div>\
            </div>\
        </div>\
        ', props: ['question', 'questionnum', 'showrefanswers', 'hidescore', 'parentqtype', 'index', 'client']
    })
}
// 加载过滤器
function onlineDoWorkLoadFilter(Vue, workStatus) {
    //全局方法 Vue.filter() 注册一个自定义过滤器,必须放在Vue实例化前面
    //数字转成字母
    Vue.filter("fromCharCode", function(value) {
        return String.fromCharCode(65 + value)
    });
    // 格式化html
    Vue.filter("formatHtml", function(val, question) {
        var swiper = swiper || window.mySwiper
        var currentSlides = swiper ? swiper.slides[swiper.activeIndex] : ''
        if (question.qtype == 4 || question.qtype == 10) {
            // return val.replace(/{#blank#}(\d*){#\/blank#}/g, '<span class="down_line" data-index="$1">($1)</span>')
            var onIndex = swiper ? parseInt($(currentSlides).find('.sub-question-item.showTag').data('index')) : ''
            return val.replace(/{#blank#}(\d*){#\/blank#}/g, function (val, index) {
                if (question.children && question.children[index - 1] && question.children[index - 1].options && question.children[index - 1].options.length == 1) {
                    if (question.children[index - 1].options[0].content) {
                        var onClass = onIndex == (index - 1) ? 'on' : ''
                        return '<span class="down_line clickToIndex ' + onClass + '" data-index="' + index + '">' + question.children[index - 1].options[0].content + '</span>'
                    }
                }
                return '<span class="down_line clickToIndex" data-index="' + index + '">(' + index + ')</span>'
            })
        }
        return val
    });
    //填空题参考答案兼容一空多解
    Vue.filter("fillingRefAnswer",function(value) {
        var val = value;
        try{
            val = JSON.parse(val)
            val instanceof Array && (value = val.join('/'))
        }catch(e){}
        return value;
    });
    //短文改错参考答案去除标点符号
    Vue.filter("removeP",function(value) {
        value = value.replace(/[\ |\~|\`|\!|\@|\#|\$|\%|\^|\&|\*|\(|\)|\-|\_|\+|\=|\||\\|\[|\]|\{|\}|\;|\:|\"|\'|\,|\<|\.|\>|\/|\?]/g, "");
        return value
    });
    
    //获取iframe调用视频的src
    Vue.filter("getIframeVideoSrc", function (url) {
        return 'https://file.dzb.ciwong.com/letv/media/' + url.substring(7) + '.mp4'
    });
}
