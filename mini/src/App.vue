<script>
import { useUserStore } from '@/stores/user'
import { initCloud } from '@/api/request'

export default {
  onLaunch() {
    initCloud()
    const userStore = useUserStore()
    userStore.silentLogin().catch((error) => {
      console.warn('Silent login failed', error)
    })
  }
}
</script>

<style lang="scss">
@import 'uview-plus/index.scss';
@import './styles/global.scss';
</style>