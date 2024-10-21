let receive_data;

const msg_input = document.querySelector('input[name=message]');
const btn_send = document.querySelector('#send_msg');

let _pk = document.querySelector('.conv').id;
let host = window.location.host

// host = "127.0.0.1:8000"
// _pk = 54

// console.log("AAAAAAAAAAAAAAA")

const url = `${host}/ws/conversation/${_pk}/`;
// const url = `${host}/ws/conversation/${_id}/`;
const noti_url = `${host}/ws/noti/${_id_op}/`;



const chatSocket = socket_connect(url);
const sendNotiSocket = socket_connect(noti_url);
const scrDiv = document.querySelector('.chat-scroll');

let bt_scrCnt = false;
let tp_scrCnt = false;
let chatTotal;

scrDiv.scrollTop = scrDiv.scrollHeight;

// 요청 후 다시 소켓에서 온 데이터 받아서 처리
chatSocket.onmessage = (e) => {
    let data = JSON.parse(e.data);
    receive_data = data['message'];

    // console.log(receive_data)

    if (receive_data.type == 'conversation' && receive_data.user.id != _id) {
        bt_scrCnt = false;
        addMessage(receive_data, 'opponent');
    }
};

chatSocket.onclose = (e) => {
    // alert(gettext("Failed to send"))
    console.log('Failed to send');
};

btn_send.addEventListener('click', async (e) => {

    e.preventDefault();

    if (!msg_input.value) return;

    let _message = msg_input.value;
    let url = `/api/v1/conversations/${_pk}/send`;
    let _data = { msg: _message };
    let _tk = document.querySelector('input[name=_csrf]').value
    let _tk_header = document.querySelector('input[name=_csrf_header]').value

    let _h = {
        [_tk_header] : _tk,
    };

    // let _h = {
    //     'X-CSRFToken': _tk,
    // };


    let result = await ajaxCall(url, 'POST', _data, _h);
    result = await result.json();

    msg_input.value = '';

    let msg_data = {
        msg: _message,
        created: undefined,
        user: {
            name: _name,
        },
    };

    if (result['success']) {
        msg_data['created'] = result['data']['created'];

        addMessage(msg_data, 'success');

        let send_data = {
            type: 'conversation',
            user: { id: _id, name: _name },
            pk: _pk,
            msg: _message,
        };

        sendNotiSocket.send(JSON.stringify({ type: 'conversation', conv_id: _pk, noti: true }));
        chatSocket.send(JSON.stringify(send_data));

        return;
    }

    addMessage(msg_data, 'fail');
});

function addMessage(_data, status, pend, _height) {
    let addClassName = '';
    let bgColoer = '';
    let stSpan = '';
    let stText = "failed";

    const spNoMsg = document.querySelector('.no-msg');

    if (spNoMsg) {
        spNoMsg.remove();
    }

    if (status == 'success') {
        addClassName = 'conv-msg self-end text-right';
        bgColoer = 'bg-teal-500 text-white';
        stSpan = !_data.is_read ? `<p class="is-read text-xs">1</p>` : '';
    } else if (status == 'fail') {
        addClassName = 'self-end text-right';
        bgColoer = 'bg-red-500 text-white';
        stSpan = `<p class="failed text-xs text-red-500">${stText}</p>`;
    } else if (status == 'opponent') {
        addClassName = 'conv-msg';
        bgColoer = 'bg-gray-300';

        let read_arr = document.querySelectorAll('.is-read');

        if (read_arr) read_arr.forEach((elem) => elem.remove());
    }

    let div = document.createElement('div');
    div.className = 'mb-10 ' + addClassName;

    let user_name = _data.user.name.length > 12 ? _data.user.name.slice(0, 12) + '...' : _data.user.name.slice(0, 12);

    let loc = 'ko'
    // console.log("AAAAAAAAA",_data);
    // console.log("AAAAAAAAA",loc);
    // let _created = loc == 'en' ? moment(_data.created_at).format('MMM. D YYYY, h:mm a') : moment(_data.created_at).format('lll');
    // let _created = moment(_data.created_at).format('lll');
    let _created = moment(_data.created_at).format('YYYY년 MM월 DD일 hh:mm A').replace('PM', '오후').replace('AM', '오전');
    _created = _created.replace('am', 'a.m.').replace('pm', 'p.m.');

    tags = `<span class="text-sm font-medium w-56 text-gray-600 truncate">
            ${user_name}
            </span>
            <div class="mt-px p-5 w-56 rounded break-words text-left ${bgColoer}">
                ${_data.msg}
            </div>
            <p class="text-xs">${_created}</p>
            `;

    div.innerHTML = tags + stSpan;

    let _div = document.querySelector('.chat-scroll');

    if (pend === 'prepend') {
        _div.prepend(div);
        return div.offsetHeight;
    }

    _div.appendChild(div);
    _div.scrollTop = _div.scrollHeight;
}

async function read_msg() {
    let url = `/api/v1/conversations/${_pk}/read`;
    let _tk = document.querySelector('input[name=_csrf]').value
    let _tk_header = document.querySelector('input[name=_csrf_header]').value

    let _h = {
        [_tk_header] : _tk,
    };
    //
    // let _h = {
    //     'X-CSRFToken': _tk,
    // };

    let result = await ajaxCall(url, 'POST', null, _h);
    return result;
}

function getCookie(name) {
    let cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, name.length + 1) === name + '=') {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}

// 스크롤 페이징 이벤트
scrDiv.addEventListener('scroll', async (e) => {
    e.preventDefault();

    if (Math.abs(scrDiv.scrollHeight - scrDiv.scrollTop - scrDiv.offsetHeight) <= 2) {
        if (!bt_scrCnt) {
            bt_scrCnt = true;

            let __msgs = document.querySelectorAll('.conv-msg');

            if (__msgs[__msgs.length - 1].classList.contains('self-end')) return;

            console.log("AAAAAAAAAAAAA")

            await read_msg();
            const opp_noti_url = `${window.location.host}/ws/noti/${_id_op}/`;
            const opp_notiSocket = socket_connect(opp_noti_url);

            opp_notiSocket.onopen = () => opp_notiSocket.send(JSON.stringify({ type: 'read', op_id: _id_op, conv_id: _pk, noti: true }));
        }
    } else if (scrDiv.scrollTop <= 0) {
        if (!tp_scrCnt && (!chatTotal || chatTotal > chatCnt)) {
            tp_scrCnt = true;

            chatCnt = document.querySelectorAll('.conv-msg').length;
            let url = `/api/v1/conversations/${_pk}/list?start=${chatCnt + 1}`;
            // let s_url = `https://airbnb-clone-dnova12222.s3.amazonaws.com`
            // let s_url = `http://localhost`;
            let s_url = `${window.location.protocol}//${window.location.host}`;

            let img = document.createElement('img');

            img.setAttribute('class', 'mx-auto mt-3');
            img.setAttribute('src', s_url + '/img/loading.gif');
            // img.setAttribute('src', s_url + '/static/img/loading.gif');

            scrDiv.prepend(img);

            await ajaxCall(url, 'GET').then(async (res) => {
                img.remove();
                if (res.status === 204) return;

                result = await res.json();

                if (result['success']) {
                    // console.log(result["data"])

                    let i = 0;
                    let height;
                    chatTotal = result['total_msgs'];

                    for (_item of result['data']) {
                        i++;
                        // console.log(_item)
                        _item.user.name = _item.user.first_name;
                        _item.msg = _item.message; //+ " " + _item.id

                        if (_item.user.id == _id) {
                            height = addMessage(_item, 'success', 'prepend');
                        } else {
                            height = addMessage(_item, 'opponent', 'prepend');
                        }
                    }
                    // console.log(i, 162 * i)
                    scrDiv.scrollTop = (height + 40) * i;

                    tp_scrCnt = false;
                }
            });
        }
    }
});
