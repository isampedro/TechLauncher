function isEmpty( input ){
    for (let i = 0; i < input.length; i++) {
        if(input.charAt(i) !== " " ){
            return false;
        }
    }

    return true;
}

function searchFrameworks() {
    let input = document.getElementById("searchInput").value

    if( !isEmpty(input) ) {
        window.location.href = "/search?toSearch=" + input;
        return;
    }
    window.location.reload();
}

if( document.getElementById("search") != null ) {
    form = document.getElementById("search").addEventListener('submit', e => {
        e.preventDefault();
        searchFrameworks();
    });
}
